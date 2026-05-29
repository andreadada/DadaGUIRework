# Architettura DadaGUI universal-jar

## Regola principale

Il progetto resta modulare, ma il prodotto finale è uno solo:

```text
DadaGUI-universal-2.3.0-SNAPSHOT.jar
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
dadagui-dist-universal/target/DadaGUI-universal-2.3.0-SNAPSHOT.jar
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

La versione `2.3.0-SNAPSHOT` aggiunge un layer dichiarativo sopra alle primitive iniziali:

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
