# Architettura DadaGUI universal-jar

## Regola principale

Il progetto resta modulare, ma il prodotto finale è uno solo:

```text
DadaGUI-universal-2.8.0-SNAPSHOT.jar
```

## Layer

```text
Plugin utente / demo
   ↓
dadagui-api                         ← contratti GUI, slot, sessioni, paginazione
   ↓
dadagui-bukkit                      ← bridge Bukkit: Inventory, ItemStack, eventi
   ↓
dadagui-version-*                   ← adapter automatici per versioni Minecraft
   ↓
dadagui-dist-universal              ← shaded jar unico
```

## Low coupling

- `dadagui-api` non importa Bukkit.
- `dadagui-api` non conosce le versioni Minecraft.
- `dadagui-bukkit` non contiene logica di paginazione.
- Gli adapter versione non contengono logica GUI generale.
- Gli slot sono oggetti riutilizzabili con handler separato.
- Il refresh è gestito dalla sessione, non dalla GUI concreta.

## Perché non usare target Java diversi nel jar universale

Un jar singolo deve poter essere caricato dal server più vecchio che vuoi supportare.

Se includi una classe compilata Java 21 in un jar destinato anche a Java 8, il caricatore classi può fallire con:

```text
UnsupportedClassVersionError
```

anche prima di arrivare alla logica di selezione adapter.

Per questo tutti i moduli inclusi nel jar universale sono compilati con target Java 8.

## Come supportare versioni nuove senza rompere le vecchie

Le versioni nuove si supportano così:

```text
1. codice comune Java 8
2. controllo versione server
3. controllo presenza classe/metodo con reflection
4. fallback sicuro se la feature non esiste
```

Esempio concettuale:

```java
try {
    Class<?> componentClass = Class.forName("net.kyori.adventure.text.Component");
    // usa capability moderna solo se presente
} catch (ClassNotFoundException ignored) {
    // fallback legacy
}
```

## Adapter presenti

```text
LegacyBukkitVersionAdapter       → 1.8.x - 1.12.x
ModernBukkitVersionAdapter       → 1.13.x - 1.21.x
Minecraft261BukkitVersionAdapter → 26.1.x
DefaultBukkitVersionAdapter      → fallback
```

## Build

```bash
mvn clean package -pl dadagui-dist-universal -am
```

## Output

```text
dadagui-dist-universal/target/DadaGUI-universal-2.8.0-SNAPSHOT.jar
```

## Estensioni future consigliate

```text
dadagui-version-v1_8
dadagui-version-v1_12
dadagui-version-v1_16
dadagui-version-v1_20
dadagui-version-v1_21
dadagui-version-v26_1
dadagui-version-v26_2
dadagui-paper-capabilities
dadagui-folia-capabilities
```

Tutte queste estensioni possono restare nel jar unico finché sono compilate a Java 8 e non referenziano direttamente classi/metodi assenti nelle versioni vecchie.

---

## Layout/Ingredient layer

La versione `2.8.0-SNAPSHOT` aggiunge un layer dichiarativo sopra alle primitive iniziali:

```text
DadaGui -> builder fluent
GuiLayout -> layout a marker
GuiIngredient -> item/slot renderizzabile
PagedLayoutGui -> paginazione layout-first
GuiScope -> STATIC / PER_PLAYER / SHARED
PageMode -> PER_PLAYER / SHARED
MaterialKey -> chiave materiale neutra rispetto a Bukkit
```

Il core rimane low-coupled perché questi componenti vivono in `dadagui-api` e non importano Bukkit.
Il modulo `dadagui-bukkit` fornisce solo factory e runtime bridge:

```text
BukkitGuiManager -> lifecycle/sessioni/eventi
BukkitIngredients -> ingredienti Bukkit pronti
BukkitNavigation -> previous/next/close
BukkitVersionAdapter -> compatibilità materiali/titoli/versioni
```

### Regola di coesione

- `dadagui-api` conosce solo concetti di GUI.
- `dadagui-bukkit` conosce Bukkit e traduce API -> Inventory.
- `dadagui-version-*` conosce solo differenze di versione.
- `dadagui-examples` dimostra utilizzo reale, ma non contiene logica framework.

---

## Slot behavior layer

La versione `2.8.0-SNAPSHOT` sostituisce il modello runtime basato su enum con composizione di comportamenti:

```text
GuiSlot<C, I>          -> item + lista di SlotBehavior
SlotBehavior<C, I>     -> Strategy estendibile per click/storage/native movement
SlotBehaviors          -> factory di comportamenti built-in
SlotType               -> solo preset/compatibilità nel builder
```

Scelta di design:

```text
GuiSlot HAS-A SlotBehavior[]
non
StorageSlot IS-A GuiSlot
ButtonSlot IS-A GuiSlot
```

Motivo: aggiungere un nuovo tipo di slot non deve richiedere modifiche a enum, switch o runtime Bukkit. Un plugin può definire un nuovo `SlotBehavior` fuori dal framework e assegnarlo a uno slot tramite builder/factory.

Pattern usati:

```text
Builder   -> costruzione fluente di GUI e slot
Strategy  -> SlotBehavior decide click/storage policy
Factory   -> SlotBehaviors, BukkitIngredients, BukkitNavigation
Adapter   -> BukkitVersionAdapter per versioni Minecraft
Facade    -> DadaGui come entry point API
Observer  -> refresh/refreshAllViewers sulle sessioni aperte
Template-like lifecycle -> open/render/click/refresh/close gestiti dal manager runtime
```

## Storage/Vault layer

Il layer storage rimane separato dal core Bukkit:

```text
StorageContainer<I>    -> contenitore mutabile platform-neutral
StorageProvider<C, I>  -> risolve quale storage mostrare al viewer
StorageSaveHandler     -> salva lo storage sincronizzato
StorageLayoutGui       -> layout con solo alcuni marker mutabili
```

La GUI non salva direttamente su file/database. La GUI espone solo i punti di integrazione:

```text
storageProvider(context -> repository.personal(playerId))
onSave((session, storage) -> repository.save(playerId, storage))
```

Questo mantiene:

- **low coupling**: la GUI non sa se lo storage è YAML, SQL, Redis o memoria;
- **high cohesion**: il runtime Bukkit gestisce eventi inventory, la repository gestisce persistenza, la GUI gestisce layout/slot;
- **sicurezza**: solo gli slot con comportamento storage permettono movimento nativo degli item.

## Gestione eventi storage Bukkit

`BukkitGuiManager` ora gestisce:

```text
InventoryClickEvent
InventoryDragEvent
InventoryCloseEvent
PlayerQuitEvent
plugin shutdown
```

Regole runtime:

- click su filler/display/button/content non-storage: protetto secondo `SlotBehavior.shouldCancelClick`;
- click su storage slot: movimento nativo permesso tramite `SlotBehavior.acceptsNativeItemMovement`;
- drag su slot non-storage: cancellato;
- shift-click dal player inventory verso la GUI: cancellato di default;
- prima di refresh/close/quit/shutdown: sincronizzazione storage visibile -> StorageContainer;
- dopo la sincronizzazione: chiamata a `StorageSaveHandler`.

## Shadable bundle module

Version `2.8.0-SNAPSHOT` adds:

```text
dadagui-bundle-universal
```

This module is not a Bukkit plugin. It has no `plugin.yml` and no example command classes. It is the artifact intended for other plugins that want to embed DadaGUI with Maven Shade.

Coordinates:

```xml
<dependency>
    <groupId>it.dadagui</groupId>
    <artifactId>dadagui-bundle-universal</artifactId>
    <version>2.8.0-SNAPSHOT</version>
</dependency>
```

---

## Entry API layer

La versione `2.8.0-SNAPSHOT` aggiunge un livello item-oriented sopra la layout API.

```text
DadaGui.pagedEntries('x')
        -> PagedEntryGui
        -> PagedEntryOpenRequest
        -> GuiOpenOptions
        -> GuiSession
```

Questo layer non sostituisce la paginazione dichiarativa con `contentProvider/contentRenderer`.
La affianca per casi in cui ogni elemento deve possedere rendering e click.

### Responsabilità

```text
PagedEntryGui
- template immutabile della GUI
- layout, marker, navigazione, scope

PagedEntryOpenRequest
- entries di quella apertura
- attributi runtime
- callback onOpen/onClose

GuiEntry
- Strategy per trasformare un oggetto in GuiSlot

GuiOpenOptions
- oggetto di trasferimento per dati runtime per sessione

BukkitGuiManager
- Facade/runtime adapter che crea la sessione e applica le opzioni
```

### Perché non `gui.addContent(...)`

`addContent` su una GUI riutilizzabile crea stato condiviso e rischia leakage tra player.
La Entry API passa il contenuto nella open request:

```java
recipeGui.open(player)
        .entries(validRecipes, factory)
        .onClose(...)
        .show(guiManager);
```

Quindi il template resta immutabile e la sessione contiene i dati runtime.
