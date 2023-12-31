﻿**Overview**:

This log retrieval service was written in Scala using Play Framework. The core business logic of reading the most recent log lines are in LogService. The algorithm makes use of java.io.RandomAccessFile to efficiently read from the bottom of a file.

**Usage**:

For example,

1) Import the sbt project in Intellij
2) Build project
3) On CLI, navigate to project and run "sbt compile run"
4) Place a log file (example can be found in log-retrieve-service/src/test/resources) in the /var/log folder on machine
5) On CLI, to fetch log lines from, for example, "/var/log/sample.log", use curl command "curl --request GET http://localhost:9000/logs/sample.log"

Feel free to reach out if you have trouble with any of the steps above

**Testing**:

You can test manually by hitting the endpoint with different params. Alternatively, refer to the unit tests located in log-retrieve-service/src/test/scala
