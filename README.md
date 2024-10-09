# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## My Server Design
[starter-code](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIAUAsCGBnGAmaBhelnOgMqQBOAbidACK4gDmAdgFCOIDGwA9sVuCJPcEYAHRMVCsQIgYRLliw0eMmJpACRUATKPJFiQEqcBll9kBXoMqjlRMEQBBVq1zJGG24gBGKGBs-N+YGIAT2QRCXpaaAAGADoATkZaYg4AVyFoAGJ6RFJg6AAlSFoQZCDbEA56LIB3eDAzTF5A6ABaAD5jOQAuaABtWAB5AgAVAF1oAHpU1GIAHXoAbwAiGZIcgFtIZehuneWAGmhlkTwarg0dveOj5cgNxBBwZevlgF9GIjISDvV6LRIvRWa2Im22u32t1OyHOxEuEJux3uj2eCPejD+AOIHS+pFMvWIxVKwBIAAoiiUyiQigBHVK4YAASk+slMHRsdkczjwvVokGAAFVZqSQWDmRyHE4XB0-N0AGIgf7QIUkCXQTz5UWILaMCVc6U41nOXr0VLgcAskzODpUDz6nmsQm2SAq4gi2YS8V2qV4GWeXr2DQaZUejyWvHW9p6n3IbqOyDO+ypYDwUmIZPwT2673c5B+7qB4NJlMS8Ns36abS9CnE6m4M2CTHaVqG77EIGrWZgq7HRHLdMpkYcADW-B7+w+uJ+HSafAEvTQ0WiC2BXe14N6kOOA-gQ9H9HHxw+-A0zGSaQymS4Kj50AAMhwStVMnUGoxZ4FWz1+kNRhNJqgeCVPQK6dus66HocxzQrC8Kbke4Y-O0TaAtAq7gVskFQigMIXJBHwodi7S4vi4CPoqpIPk+tL0mUzIkZG0a5ry-Kuu6GGQF6nIxvmCpKq6aoatAWo6gxkBfvi0DxomGZ7vwpL0Ua4lRjmLgscAxapjucn0Fxkq5vmADi-LQJpgn5NpI78Nm3EGRJxrQKa5ploxqkOk6JKaWmGZZkx0rtLKhamT5YZiS2yGVqhVGKkUyANhikXYl+qHoaCEG7L2UH9rJVkHmik6yNO7QfvO0CLsuSxgWlmEZVu2WDrlVwTowJ5nik6RZMkkD8Pej5pEYL71CS77NNINpTu2-SUAAone00jNN-6AcgwELDuXAgAAXhUVS9AAPJZ+7tIhRGER2h1jvlCX-M2xFKb0ZG0P1lF9cmNEMopVrKX5PLQHyGkZt5DX7np9p5gF-rGUYZkeOqFk5fuLnfW5sbQBokBQJ5gMXbpNn6f5srUJjMAw3YcPQDjSPhWdvVPW99bgI2iXhRNQIFW24UlcAC5Liux7-G1F5ZISwZ3sS0CGeu+CDW+XNtJ0rP9IZ83-rQ65rRmG3baAu3QAdCP8MdE0VjdKX1bujVXYR9mQA9xJq1syCUcSkuO+9dFUyptlqX9-JeTjoM8RDvR8UWIVk0JlNhV7+O-bwZSu7gCl42D+YSyZiDmhL66lj9eY29W-KpKC0Dx0YHAAGbZ1spZhSbWK9GLCdS7F8XW8lk0rA7uAvP0XfrgAkpQvcAIxoAAzAALLcr4kq63avEcCzLJ44BsMO88QYvxzdwAcuurxvGM7NyJzo3c2VvOVd3yC930-dbEPo8T9Pxyzy6a6Ydvy+r+vm9f3VPeB8JzHxagLJI7VLxCGLkIKAWAPIwETrUIajRz7yy6ICAYwxxhTG7hrFMWsdr0H2jjdooEgEAIQsbCKpt2wP0gPvGq29zY6Sagha2d0vq9GkiSROpJMAIMTu7Jkns87qX9gbXGec06h2CiWWGkdJGezEtwwRg9KDJ2jmI32wA+HdyHoHAywdoCyMTuZP66iU48QLo5M0FotEo1UQmXh65SQUMgEcfRlBDEE39KZIM1dIC1yUtTRKvQBHOMgEIhmTNaEs0Kp3ZYXjn5TxPkVLmPMKr0Kfr0MeqSwGnggULTIwQMZkRqNAAAUhwRUgSsi-1YMOEac4jDjQSb0AYAocGTDwTkTWxAtpEJIZIshlVYGIFKcQTAHAyLEBeMsAA6qoAeC1JgACE7z2EwAAaSysk3JL80mnTCSscZkzpmzPmUslZ011mbJ2Xs9RKTX7Lxxr3ZqHCVHQAAFY1PoHw6pipol0g+qIxxOiJHA34D430xjZGk0QOTKOISY5g3Uno9RMLwayhMYqYMZiFH5C8WC72v0BRCHcC4rYbj1FHDOSQC5XAjgiU4lYoxOLyWUsQTnWGnBoCKlYOAVI6NhKzCpvXKsVS-nRLioza6WJ4ltjZidM+LTMl8wKYLDqmRkgTJ4AmbgiAhCwP0EQ5Bss0FtKVVNWa81FpTD8CqmhDc0IEWZpwiMtspJQFEPYY1vBWBEP4T64gfqhDCM+p69k4LBUGuTtI4xRN+QwEzuAORmZQoou0bG0Q8aUZpyTSSCmWcCV2BJbHVGOa3RYoLRjZNxa00CUzV9UJtDwkhrDa3OV7cPUpSOaqwI6qlj81PEAA)
