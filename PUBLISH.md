To publish Dueuno Elements to Maven Central execute the following. Do not execute the tasks separately, it will not work (!).
```
./gradlew clean publishToSonatype closeAndReleaseSonatypeStagingRepository
```

Then go to:
```
https://central.sonatype.com/publishing/deployments
```

And verify the state of the publishing