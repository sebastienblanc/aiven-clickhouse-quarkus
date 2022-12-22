# Using Clickhouse for Aiven with Quarkus

## Pre-requisites

You need a Clickhouse instance, you can create one for free with the [Aiven Trial](https://console.aiven.io/signup).

You need the Cell Towers data, you can create a free account [here](https://www.opencellid.org/) and download the data from the website. 

## Load the data

From your Aiven console, in your ClickHouse instance, create first a table : 

```

CREATE TABLE cell_towers
(
    radio Enum8('' = 0, 'CDMA' = 1, 'GSM' = 2, 'LTE' = 3, 'NR' = 4, 'UMTS' = 5),
    mcc UInt16,
    net UInt16,
    area UInt16,
    cell UInt64,
    unit Int16,
    lon Float64,
    lat Float64,
    range UInt32,
    samples UInt32,
    changeable UInt8,
    created DateTime,
    updated DateTime,
    averageSignal UInt8
)
ENGINE = MergeTree ORDER BY (radio, mcc, net, created);

```

Then from your terminal use the Docker ClickHouse Client to import the data : 

```

cat cell_towers.csv | docker run --interactive            \
--rm clickhouse/clickhouse-server clickhouse-client \
--user avnadmin                     \
--password $PASSWORD                 \
--host $HOST                         \
--port $PORT                         \
--secure                            \
--query="INSERT INTO cell_towers FORMAT CSVWithNames"

```

Be sure to be in the folder that contains your CSV file and to set `$PASSWORD`,`$HOST` and `$PORT`.

Omport can take some time depending on your connection.

## Configure the Quarkus app

In your `application.properties` be sure to set `$USERNAME`, `$PASSWORD`,`$HOST` and `$PORT`.

Run the app : `mvn quarkus:dev`

Make a call : `curl localhost:8080/towers/208` , this should give you the number of cell towers in France. It's using the MMC Codes for the countries, you can find them [here](https://mcc-mnc-list.com/list)


