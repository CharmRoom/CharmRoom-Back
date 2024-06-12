#!/bin/bash
CURRENT_PID=$(pgrep -f .jar)
if [ -z $CURRENT_PID ]; then
	echo "Server is not running now"
else
	echo "kill Server"
	kill -9 $CURRENT_PID
	sleep 3
fi

chmod +x ~/cicd/*.jar
nohup java -jar ~/cicd/*.jar > /dev/null 2> /dev/null < /dev/null &
echo "Deploy Successfully Finished!!!"

