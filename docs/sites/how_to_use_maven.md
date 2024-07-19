# How to use Maven

[<kbd>&larr; Go Back</kbd>](../../README.md)

### Run the project with Maven

In the base-directory enter:

```
mvn clean javafx:run
```

### Create a .jar-file with Maven

Again, in the base-directory enter:

```
mvn clean package
```

You will see, a .jar-file is created in the target-directory.

Run the .jar-file with the following command.

```
java -jar .\target\Pruefungsgenerator-1.0-SNAPSHOT-spring-boot.jar
```

Alternatively, you can double-click the .jar-file in the explorer or
call the .jar-file via .bat or script-file.