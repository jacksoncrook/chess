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

[![Sequence Diagram](Phase-2-Sequence-Diagram.png)](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afJCIJqTsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3l5d5LsKYoSm6MpymW7xKpgKrBhqbrarq+r+UYEBqGgADkzBWmigW8sFlnWT2fbbh5ln+gAQiGzlqGAUYxnGhRaUmlSiWmeEETmqh5vM0FFiW9R6OuKKEvVDb0QVC4Cm1HZWS6G7ulu8XqktRqPDAxkAul2XMMcYAgPE5UgdU-rMtMl7QEgABeKAcI1KCxgp6HwsmyCpjA6YAIzdfyfUFmMg3QPUPiXXq113bsdFNu5grDvyY4Tigz7xOel7Xgji6VA+a4BhjW4nXNOmlo54oZKoAGYCTp0SWBhH6fMJGod8FFUfWzO0a9mEfdhMC4fhowMzFxGkazl7s8hnNoVNnjeH4-heCg6AxHEiRKyrjm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDGziHoNzH7mf6dtIdTzvwr6zpdjZQnaw5funnVJJE1St6IzAjJgKj6PwfbaBzkFDohaK4pPgT8iyjW8dxaqiVajqerGpeMCQEh6WZTlMB5Tk037rTC0+6V-Zw3N+uSuD8SQ-dj3PfGrXvSUYCdb9Qs9QDA3FiDCqd930ONgxWOzcVi2o6+3vw+H9JGCg3DHpesdXtoieFcnOPCjIu9MoYa-aFnpc563Tuwv6WtB-+CCAR7De6ZpZ1ve1Xmw9+ZdVGDDBictmL+BROufw2BxQan4miGAABxJUGhdaVVLA0FBpsLb2CVLbCWj9-7aQ9vUV2DsaYrx9sgHIaCcwYhQUyBhahg6uVDjILeo5I5MhjpQhOddgrn1ThKW+mcoplwdqtfOGFC76gog-cuHAMqqD2tXa0tcl4NxKr2FuG827-1dLPKAt0e7Ries1R2g9Po-T+rmfMk8hozwonPSaTZtGGM7PUcRwBOFL3qHQsArDVAYhPjNe8F8wrriCTACAAAzSO6CibPy-PUZhwT0EUyptQrBLxlgEJzAWBo4xCkoAAJLSALN9cIwRAggk2PEXUKA3Scj2N8ZIoA1StMgosb4ZSABySo+kXBgJ0P+dMeZDxwqAkYBT0HFNKUqSp1Tan1OWI05pPSDJjA6QgLp2z+q7JBIM4ZuzRnjIXoxeWAQOAAHY3BOBQE4GIEZghwC4gANngMjVBFYihAL1kYxorQOj4MIZ3SWWZTlzAmYmSoJMKHEKQtCpUQzYXuxfp7ea1lDxyBQKwjEcBkasPYW5AxYck7byjvw5F6Bwn1xTtE-GL476SJzjIpayUi6KKkWgCuaiq41ywJ4mh3Y9EpK9nTYxrjTFQ17lYgegDplfScKPbM-1HGFinqWMGsqzHzymqKqVjcfEZz8a3Slp9t54vRISspmNuGRJ8XjVh69OztkRT8o8BKlTZM-pir8P8wLzLmCs+oNS6kwDhRhOaHUQGCzmWMMp4aYCRsCNGq5kCFaWF3rZTYqskAJDALmvsEAC0ACkIDij+XMGInSQBqgBUPIF0qQXMhkj0MpRC44otGNgfZuaoBwAgLZKAawU3SBjdir1AiswDuAEOkdY6J3LKnYGiyJrrIACtq1oEJVW8UpKUDjRDparhVKeE0oPgIhlwihSiPTqyiR2c3acqSvI4uRc+UCvUcKoRycxUwGbpK+a50TEGoVS9JV8AgEj3sb1LVQMdWgwg1Ddxi8nVeNNSyo+8h-HcJ8pkuYGJJ13rPg+mAzLWH30qbnBKtbDCfsqb+oVmiRVYaAyBp+Jrqq1RPS5KD-dSFxrg6qxNYxx5IeBqWEaMAxouQwwBxcQG3UrTzlUbg4B7ql2DDMSsZoazhhagYvJAYqyhmM0JkzkzRMqvTJmMemr+raucSaQzpdjNKZ4w8LF9RD37r9R-L+WLg0vGnVMz6AsszgOuVArwi7C3FoS-KRAwZYDAGwAOwgeQCgwGbeYVtNQDZGxNmbC2xhHYIvIZfPeKB2EbtjVuxaIBuB4B5HoAwRK2vLXkJ1+rAn6rko9Z5a1PDWvpbCcp2aIiYARhmLGeUHX9BiB816tL7XtD9f9SFoN2Hf7WOVVF2ZVwrlAA)