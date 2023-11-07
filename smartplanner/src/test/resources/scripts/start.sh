@echo OFF
export OTP_HOME=$PWD
export ROUTERS=trentino,bologna
echo OTP_HOME is $OTP_HOME
echo "Starting smartplanner distributed instance."
cd $OTP_HOME
screen -d -m -SL smartplanner-dist java -Dinitialize=true -Dserver.port=5055 -Dserver.contextPath=/smart-planner -jar lib/smart-planner.jar
echo "Starting OpenTripPlanner distributed instance."
screen -d -m -SL otp-dist java -Duser.timezone=Europe/Rome -jar lib/otp.jar --basePath . --graphs . --port 5555 --securePort 6566 --server --router trentino --router bologna --autoReload