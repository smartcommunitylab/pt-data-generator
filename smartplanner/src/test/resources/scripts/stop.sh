@echo OFF
echo "Stopping Smart Planner instance."
ps -ef | grep smartplanner-dist | grep -v grep | awk '{print $2}' | xargs kill
ps -ef | grep otp-dist | grep -v grep | awk '{print $2}' | xargs kill