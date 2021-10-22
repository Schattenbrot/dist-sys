# Distributed Systems

For distributed systems practical exercise.

## Usage

2 Ways of using:

- docker-compose as quick start with pre-configured containers
- docker for starting containers individually

### With docker-compose

Start the containers from the default root (use --build if there are fragments already existing):

> docker-compose up --build

To remove the containers + fragments (used/created images and networks):

> docker-compose down --rmi all

### With docker

#### Network

Create a new network for the server and sensors to live in:

> docker network create network-name

#### Server

Navigate from root-folder to the (go-)server:

> cd server

Build the server image with:

> docker build -t name-of-server-image .

Start the server with:

> docker container run --network network-name --name server-name name-of-server-image

- server-name needs to be server for now until the sensor got fixed.

#### Sensor

Navigate from root-folder to the sensor in the sensor-folder:

| sensor-name                 | cd command                    | target sensor                                          |
| --------------------------- | ----------------------------- | ------------------------------------------------------ |
| [mvn-sensor](#mvn-sensor)   | `cd sensor/mvn-sensor/sensor` | java maven sensor (default used in the docker-compose) |
| [go-sensor](#go-sensor)     | `cd sensor/go-sensor`         | go sensor                                              |
| [java-sensor](#java-sensor) | `cd sensor/plain-java-sensor` | java sensor (minimalistic, no json)                    |

##### MVN Sensor

Wants environment variables:
| variable | data-type | default |
| `SENSOR_TYPE` | enum (LAGER, LADEN) | LAGER |
| `SENSOR_VALUE` | int | 0 |

- SENSOR_TYPE will get changed to RFID, BARCODE
- SENSOR_VALUE will get changed to use the `Code-11` format which uses 1234567890- as charset.

To build image:

> docker build -t sensor-name .

To run container:

> docker container run -e SENSOR_TYPE=typeval -e SENSOR_VALUE=val --network network-name sensor-name

- environment variables are optional
  - typeval needs to be "STORAGE" or "SHOP"
  - val needs to be any integer.
  - if error in environment variables it will initialize with the default values.
- `--network network-name` used network needs to be the SAME that is used on the server!! Otherwise it won't work and just try to find the server every 2 seconds permanently until the user ends the poor sensor.
- It will (for now) ALWAYS look for the dns of the server called "server"! So server needs to be called the same (will get changed in the future to another env variable)
- env vars will maybe just get moved to args for ease of use.

##### GO Sensor

Stuff ...

##### Java Sensor

Even more stuff ...

## Known bugs

- Re-try loop for dns-search fails in mvn-sensor:

```
Exception in thread "main" com.evanlennick.retry4j.exception.UnexpectedException: Unexpected exception thrown during retry execution!
        at com.evanlennick.retry4j.CallExecutor.tryCall(CallExecutor.java:146)
        at com.evanlennick.retry4j.CallExecutor.execute(CallExecutor.java:89)
        at com.evanlennick.retry4j.CallExecutor.execute(CallExecutor.java:60)
        at com.schattenbrot.sensor.Sensor.run(Sensor.java:114)
        at com.schattenbrot.sensor.App.main(App.java:11)
Caused by: java.net.UnknownHostException: server: No address associated with hostname
        at java.base/java.net.Inet4AddressImpl.lookupAllHostAddr(Native Method)
        at java.base/java.net.InetAddress$PlatformNameService.lookupAllHostAddr(Unknown Source)
```
