# Serverless Telegram Bot
## Demo Project for Online Stream #3 (English)

Application is build with [Quarkus](https://quarkus.io/).

It runs live on AWS Lambda, receives hooks from Telegram via API Gateway.

The Telegram Bot can be used here: [@demo_tkach_weather_bot](https://t.me/demo_tkach_weather_bot)

## Access to Online Stream

You can view the online stream on YouTube after making a donation to support my 
volunteering initiative to help Ukrainian Armed Forces.

:coffee: Please, visit the [Buy Me a Coffee](https://www.buymeacoffee.com/ytkach/e/223988).

Thank you in advance for your support! Слава Україні! :ukraine:

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/uIh01uFJb2M/0.jpg)](https://www.youtube.com/watch?v=uIh01uFJb2M)

## Running the application in dev mode

Before running the application you need to set the API KEY for geo coding service
and a Telegram bot token to verify requests from Telegram.

You can do this by setting the values in `application.yml` file or with environment variables:
```shell script
APP_GEO_CODE_API_KEY=your-api-key
APP_TELEGRAM_SECRET_TOKEN=your-bot-token
```

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/demo-3-en-telegram-weather-bot-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- RESTEasy Classic's REST Client ([guide](https://quarkus.io/guides/rest-client)): Call REST services
- YAML Configuration ([guide](https://quarkus.io/guides/config-yaml)): Use YAML to configure your Quarkus application
- AWS Lambda Gateway REST API ([guide](https://quarkus.io/guides/aws-lambda-http)): Build an API Gateway REST API with Lambda integration
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more
