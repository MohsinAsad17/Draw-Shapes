# Draw-Shapes

A drawing application built with Java/Swing that replicates core features of Microsoft Paint. Supports drawing geometric shapes, freehand curves, and applying fills — with a branching undo/redo history system.

> Built using [Gemini CLI](https://github.com/google-gemini/gemini-cli).

---

## Features

- **Shapes:** Rectangle, Circle, Triangle
- **Lines:** Straight line, Freehand curved line (path recorded from mouse drag)
- **Fill Tool:** Click any existing shape to apply a fill color
- **Stroke options:** Color, thickness, and style (Solid, Dashed, Wavy)
- **Selection & resize:** 8-point handle grips for scaling shapes
- **Branching undo/redo:** Full history navigation — new actions create a branch rather than wiping the redo stack
- **Save/Load:** JSON-based file persistence via file chooser

---

## Screenshots

<!-- Add a screenshot or GIF here -->
<!-- ![Draw-Shapes demo](docs/screenshot.png) -->

---

## Getting Started

### Prerequisites

- Java 21
- Gradle

### Run

```bash
git clone https://github.com/MohsinAsad17/Draw-Shapes.git
cd Draw-Shapes
gradle shadowJar
java -jar build/libs/DrawShapesApp-all.jar
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| GUI | Swing |
| Build | Gradle + Shadow plugin |
| Persistence | JSON |

---

## Design Document

See [`docs/DESIGN.md`](docs/DESIGN.md) for the full technical design, including data schema, behavioral specs, and the branching history implementation notes.

---

## License

MIT
