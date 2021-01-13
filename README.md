# chatr

An HTML5 chat application

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Dev

Start a repl and the server will be running on the configuration port,
default 8080

### Uberjar

You can also run the uberjar 

`lein with-profile +prod uberjar`

Then run the server

`scripts/run-server-jar target/chatr-server.jar`

### Docker

Running locally with docker

`docker-compose -f docker/server/docker-compose.yml up --build -d`

bring it back down

`docker-compose -f docker/server/docker-compose.yml down`

## Deploying

1. Compile the uberjar


# Assets
## Logo
https://www.freelogodesign.org/preview?lang=en&name=chatr&logo=bac2fefa-a2f2-4a46-914e-7468f4f74398

## License

Copyright © 2021 James Borden
