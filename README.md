# Draw B-Spline curves
Interactive B-Spline demo. Just mouse drag the points and watch the beautiful curves moving.

Originally a Java applet (circa 2010), converted to a standalone Swing app in 2023.

![Screenshot](screenshot.png)

## Background

This project was originally written as a **Java applet** around 2010 — a technology that let Java programs run inside web browsers via a plugin. Java applets were once everywhere: powering interactive demos, games, and visualizations on the early web.

### The death of Java applets

Java applets were deprecated in Java 9 (2017) and fully removed in Java 11 (2018). Browsers had already been dropping plugin support years earlier — Chrome removed NPAPI plugin support in 2015, and other browsers followed. This was a rare case of the web ecosystem actually breaking backwards compatibility. The usual rule of thumb is that you *cannot* deprecate anything that runs in a browser, because doing so would break a massive long tail of existing websites. This is why JavaScript still carries decades of quirky syntax and behavior (e.g., `typeof null === "object"`, loose equality coercion, `var` hoisting) — unlike Python, which made a clean break from 2 to 3, the browser can never make that kind of breaking change. Java applets were the exception: they ran via a *plugin*, not native browser code, so browsers could drop the plugin without breaking the web itself. The applets just stopped working.

### Swing to the rescue

In 2023, this project was revived by converting it from an applet to a **Java Swing** application. Swing is Java's built-in GUI toolkit — it has been part of the JDK since Java 1.2 (1998) and still works today. The conversion was straightforward: the rendering and interaction logic stayed the same, but instead of running inside a browser plugin, the app now opens its own `JFrame` window and runs as a standalone desktop program.

## How to run
```
$ make run
```
Only requires a Java runtime (`java`). To recompile after editing the source, run `make compile` (requires `javac` / a JDK):
```
$ sudo apt install openjdk-21-jdk-headless
```

## Versions
- OpenJDK 21.0.10
- Ubuntu 24.04
