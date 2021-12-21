#Use OpenDDK 14 image as the base image
FROM openjdk:14

#Create a new app directory for my application files
RUN mkdir /app

#Copy the app files from host machine to image filesystem
#COPY /Users/francisbostock/Code/javafx-sdk-11.0.2 /app/lib/
COPY Taskboard/target/classes /app

#Set the directory for executing future commands
WORKDIR /app

# Run the Main class
#CMD java -p /Users/francisbostock/Code/javafx-sdk-11.0.2/lib --add-modules javafx.controls -XX:+ShowCodeDetailsInExceptionMessages fjdb.mealplanner.MealPlanner
#CMD java -classpath src/main/java fjdb.mealplanner.MealPlanner
CMD java fjdb.mealplanner.MyMain
