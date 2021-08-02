# Contribution Guidelines

## Code Formatting

This project uses the [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/HEAD/plugin-maven) to verify format of our code, as well as auto-format it after changes are made.

You can do this by running:

```sh
mvn spotless:apply
```

As part of your local testing, we recommend running the below, to make sure your code is conformant:

```sh
mvn clean spotless:apply package
```
