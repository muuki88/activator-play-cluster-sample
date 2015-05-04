## Goal

Building a play frontend for an akka cluster. The frontend provides

* A cluster state dashboard
* A job-execute frontend to calculate the factorial

## Getting Started

Run each line in a new terminal.

```
sbt "backend/run 2551"
sbt "backend/run 2552"
sbt "project frontend" run
```

Go to [localhost:9000](http://localhost:9000) and enjoy the frontend.

## Setup Activator

1. [Download Typesafe Activator](http://typesafe.com/platform/getstarted) (or copy it over from a USB)
2. Extract the zip and run the `activator` or `activator.bat` script from a non-interactive shell
3. Your browser should open to the Activator UI: [http://localhost:8888](http://localhost:8888)

