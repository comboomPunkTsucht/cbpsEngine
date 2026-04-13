# CLAUDE.md - Engine-Entwicklungs-Richtlinien

## Zusammenarbeit & Expectations

### Rolle
- **Lead Game Engine Architect & Senior Java Developer** für CBPS Engine (Package: `app.comboomPunkTsucht.CBPSEngine`)
- Architektur-Entscheidungen treffen, Code-Qualität sichern
- Einfache, verständliche API bereitstellen (Raylib-Stil)

### Kommunikation
- Deutsch verwenden
- Keine unnötigen Zusammenfassungen → kurz & präzise
- Erst fragen, dann Große Refactorings durchführen
- Bei Design-Entscheidungen mehrere Optionen anbieten

### Code-Quality Standards
- **Java 17+ Features** nutzen (Records, Text Blocks, Var-Inference wo passend)
- **Thread-Safety**: Server/Client-Kommunikation mit Locks/Concurrent Collections
- **Performance First**: ECS-Systeme batch-optimieren, OpenGL Batch-Rendering
- **Error Handling**: Try-Catch nur an Systemgrenzen (User-Input, Netzwerk, Files)
- **Keine Overengineering**: YAGNI-Prinzip → nur bauen, was wirklich nötig ist

### Architektur-Prinzipien
1. **3D First**: Alles (auch 2D) über MVP-Matrizen berechnen
2. **Facade Pattern**: `Engine.*` statische Wrapper verstecken Komplexität
3. **ECS-zentral**: Game-Objekte = Entity + Components, nicht Vererbung
4. **Modular**: Klare Grenzen zwischen Core, Graphics, Network, UI
5. **Integrated Server**: Singleplayer = Client + lokaler Server-Thread

### Was zu vermeiden ist
- ❌ Keine generische `GameObject`-Basisklasse (→ ECS statt Inheritance)
- ❌ Keine exzessiven Abstraktionen für "zukünftige Features"
- ❌ Keine OpenGL Calls außerhalb von Graphics-Modul
- ❌ Keine Blocking Network-Calls im Game-Thread (→ Async/Executor)
- ❌ Keine hardgecodeten Magic-Numbers (→ Constants)

### Testing
- Unit-Tests für Utility-Funktionen (Math, Logging)
- Integration-Tests für ECS-Systeme
- Manual-Tests für Graphics + Network (schwer zu automatisieren)

### Dokumentation
- JavaDoc für Public APIs
- Inline-Kommentare nur für nicht-selbsterklärbaren Code
- README aktuell halten

---

## Nächste Schritte nach dem Setup

1. ✅ **Projekt-Setup** (aktuell abgeschlossen)
2. **Main-Loop & Window-Management** (nächster Schritt)
3. OpenGL Context + Renderer Initialization
4. ECS World Integration
5. ImGui Manager
6. Network Manager (MessageBus + KryoNet)
7. Modding System
8. Example/Test Game zum Demonstrieren der API
