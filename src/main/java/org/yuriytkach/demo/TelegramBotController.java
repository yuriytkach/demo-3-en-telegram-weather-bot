package org.yuriytkach.demo;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionType;
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/telegram")
public class TelegramBotController {

  private static final String PLEASE_INPUT_LOCATION = "🔡 Please, input location";

  @Inject
  StatusService statusService;

  @Inject
  AppProperties appProperties;

  @Inject
  @RestClient
  GeoCodeClient geoCodeClient;

  @POST
  public Response processHook(
    @HeaderParam("X-Telegram-Bot-Api-Secret-Token") final String secretTokenHeader,
    final Update body
    ) {
    if (appProperties.telegramSecretToken().equals(secretTokenHeader)) {
      log.info("Received update: {}", body.getUpdateId());

      if (body.hasMessage()) {
        final var response = processMessage(body.getMessage());
        return Response.ok(response).build();
      }

      if (body.hasCallbackQuery()) {
        final var response = processCallbackQuery(body.getCallbackQuery());
        return Response.ok(response).build();
      }

      return Response.ok().build();
    } else {
      return Response.status(403).build();
    }
  }

  private Object processCallbackQuery(final CallbackQuery callbackQuery) {
    return switch (callbackQuery.getData()) {
      case "input_btn" -> SendMessage.builder()
        .chatId(callbackQuery.getMessage().getChatId())
        .text(PLEASE_INPUT_LOCATION)
        .replyMarkup(ForceReplyKeyboard.builder().forceReply(true).inputFieldPlaceholder("City, Country").build())
        .build();

      case "location_btn" -> SendMessage.builder()
        .chatId(callbackQuery.getMessage().getChatId())
        .text("📤 Please, send location")
        .replyMarkup(ReplyKeyboardMarkup.builder()
          .keyboardRow(new KeyboardRow(
            List.of(KeyboardButton.builder().requestLocation(true).text("Please, tap here to send a location").build())
          ))
          .build())
        .build();

      default -> SetMessageReaction.builder()
        .chatId(callbackQuery.getMessage().getChatId())
        .messageId(callbackQuery.getMessage().getMessageId())
        .isBig(true)
        .reactionTypes(List.of(new ReactionTypeEmoji(ReactionType.EMOJI_TYPE, "🤔")))
        .build();
    };
  }

  private Object processMessage(final Message message) {
    if (message.hasLocation()) {
      final Location location = message.getLocation();
      return sendMessageWeatherForLocation(message,
        location.getLatitude().floatValue(),
        location.getLongitude().floatValue()
      );
    }

    if (message.getReplyToMessage() != null
      && PLEASE_INPUT_LOCATION.equals(message.getReplyToMessage().getText())
      && message.getReplyToMessage().getFrom().getUserName().equals("demo_tkach_weather_bot") ) {

      final var locationString = message.getText();
      final List<GeoCodeClient.GeoCodeData> geoLocationData = geoCodeClient.getGeoLocationData(
        locationString,
        appProperties.geoCodeApiKey()
      );

      if (geoLocationData.isEmpty()) {
        log.debug("Location not found: {}", locationString);
        return SendMessage.builder()
          .chatId(message.getChatId())
          .text("Location not found")
          .build();
      } else {
        log.debug("Geo location data: {}", geoLocationData.get(0));
        return sendMessageWeatherForLocation(
          message,
          geoLocationData.get(0).getLattitude(),
          geoLocationData.get(0).getLongitude()
        );
      }
    }

    return switch (message.getText()) {
      case "/start" -> SendMessage.builder()
          .chatId(message.getChatId())
          .parseMode("MarkdownV2")
          .text("""
            Hello\\! I'm *The Weather Bot*\\!
            Please, specify how you want to provide location:
            """)
          .replyMarkup(InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(
              InlineKeyboardButton.builder().text("⌨️ Input Location").callbackData("input_btn").build(),
              InlineKeyboardButton.builder().text("🚀 Send Location").callbackData("location_btn").build()
            ))
            .build()
          )
          .build();

      default -> SetMessageReaction.builder()
        .chatId(message.getChatId())
        .messageId(message.getMessageId())
        .isBig(true)
        .reactionTypes(List.of(new ReactionTypeEmoji(ReactionType.EMOJI_TYPE, "🤔")))
        .build();
    };
  }

  private SendMessage sendMessageWeatherForLocation(
    final Message message,
    final float lat,
    final float lon
  ) {
    log.info("Sending weather for location: {}, {}", lat, lon);
    final var weatherData = statusService.getWeatherDataForGeolocation(lat, lon);

    return SendMessage.builder()
      .chatId(message.getChatId())
      .text("""
          📍 %s, %s
          🌡️ Temperature: %s
          😸 Feels like: %s
          """.formatted(
          weatherData.getCity(),
          weatherData.getCountry(),
          weatherData.getTemperature(),
          weatherData.getApparentTemp()
        )
      )
      .build();
  }

}
