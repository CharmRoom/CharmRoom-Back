#!/bin/bash
CURRENT_PID=$(pgrep -f .jar)
if [ -z $CURRENT_PID ]; then
	echo "Server is not running now"
else
	echo "kill Server"
	kill -9 $CURRENT_PID
	sleep 3
fi

JAR_PATH="~/cicd/*.jar"
chmod +x $JAR_PATH
nohup java -jar $JAR_PATH > /dev/null 2> /dev/null < /dev/null &
echo "Deploy Successfully Finished!!!"

