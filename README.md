# DadaGUI

**DadaGUI** è un framework modulare e low-coupled per creare GUI Bukkit/Spigot/Paper in modo semplice, riutilizzabile e compatibile con più versioni di Minecraft.

Il progetto è pensato per avere:

- GUI statiche;
- GUI dinamiche generate a runtime;
- GUI paginate;
- GUI storage/vault con slot mutabili solo dove decidi tu;
- slot riutilizzabili con click handler;
- refresh della vista;
- separazione tra API, runtime Bukkit e adapter di versione;
- un solo jar finale universale da mettere nella cartella `plugins/`.

> Obiettivo: scrivere la logica della GUI una volta sola, lasciando al framework il compito di adattarsi alla versione del server.

---

## Stato del progetto

Questa è una base framework refactorizzata a partire dal progetto DadaGUI originale.

La struttura è pronta per essere estesa e testata su una matrice reale di versioni Minecraft.  
Il design è già impostato per supportare un jar unico universale tramite adapter automatici, ma la compatibilità completa va validata con test su server reali.

---

## Caratteristiche principali

### GUI statiche

Usa `StaticGui` quando la GUI ha titolo, dimensione e slot fissi.

```java
Gui<Player, ItemStack> gui = StaticGui.<Player, ItemStack>builder("Menu", 27)
        .slot(13, GuiSlot.<Player, ItemStack>builder(item)
                .onClick(ctx -> ctx.viewer().sendMessage("Click!"))
                .build())
        .build();

guiManager.open(player, gui);
```

### GUI dinamiche runtime

Usa `DynamicGui` quando titolo, dimensione o contenuto dipendono dal player, dallo stato o da dati runtime.

```java
Gui<Player, ItemStack> gui = new DynamicGui<>(
        player -> "Menu di " + player.getName(),
        player -> 27,
        context -> {
            context.setSlot(13, GuiSlot.<Player, ItemStack>builder(item)
                    .onClick(click -> click.session().refresh())
                    .build());
        }
);
```

### GUI paginate

Usa `PaginatedGui` quando devi mostrare liste di elementi, shop, categorie, players, kit, reward, oggetti configurabili o dati caricati da database/file.

```java
Gui<Player, ItemStack> gui = PaginatedGui.<Player, ItemStack, String>builder("Lista", 54)
        .contentSlotRange(10, 43)
        .dataProvider(player -> items)
        .itemFactory((player, value, index) ->
                GuiSlot.<Player, ItemStack>builder(createItem(value))
                        .onClick(ctx -> ctx.viewer().sendMessage("Hai cliccato " + value))
                        .build()
        )
        .previousControl(45, (page, maxPage) ->
                GuiSlot.<Player, ItemStack>builder(previousItem).build()
        )
        .nextControl(53, (page, maxPage) ->
                GuiSlot.<Player, ItemStack>builder(nextItem).build()
        )
        .build();
```

### Refresh vista

Ogni sessione può ricaricare la GUI senza doverla ricreare manualmente.

```java
context.session().refresh();
```

Questo è utile per:

- aggiornare quantità;
- cambiare pagina;
- mostrare dati aggiornati;
- ricaricare una GUI dopo un click;
- riflettere modifiche runtime senza chiudere e riaprire il menu.


---

## API dichiarativa consigliata

La nuova API permette di descrivere la GUI con un layout a caratteri, simile al sistema:

```java
Gui<Player, ItemStack> recipesGui = DadaGui.<Player, ItemStack, String>paginated('x')
        .title("Pick The Recipe To Craft")
        .layout(
                "# # # # # # # # #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # < # > # # C")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('<', navigation.previousPage())
        .ingredient('>', navigation.nextPage())
        .ingredient('C', navigation.close())
        .contentProvider(context -> recipeService.getRecipesFor(context.viewer()))
        .contentRenderer((context, recipe, index) -> ingredients.clickable(
                MaterialKey.CRAFTING_TABLE,
                recipe.getName(),
                click -> recipeService.select(click.viewer(), recipe)))
        .emptyIngredient(ingredients.display(MaterialKey.GRAY_STAINED_GLASS_PANE, " "))
        .scope(GuiScope.PER_PLAYER)
        .pageMode(PageMode.PER_PLAYER)
        .build();
```

Significato dei marker:

```text
# = filler o decorazione
x = contenuto paginato
< = pagina precedente
> = pagina successiva
C = chiusura GUI
```

Questa API è la via consigliata perché separa bene le responsabilità:

- il layout descrive la struttura visiva;
- gli ingredienti descrivono item e comportamento;
- il content provider fornisce dati runtime;
- il content renderer trasforma i dati in slot cliccabili;
- Bukkit rimane confinato nel modulo `dadagui-bukkit`;
- le versioni Minecraft rimangono isolate negli adapter.

---

## GUI storage / vault

Per vault, backpack e contenitori virtuali non devi rendere tutta la GUI modificabile.
Scegli un marker, per esempio `v`, e solo quegli slot ricevono il comportamento `storage`.
Tutto il resto resta filler, bottone, display-only o locked.

Nota di design: `SlotType` esiste ancora come scorciatoia del builder, ma non è più il modello centrale.
Uno slot ora **ha comportamenti** (`SlotBehavior`) componibili: questa scelta segue composition over inheritance, evita grandi `switch` su enum e permette di aggiungere nuovi comportamenti fuori dal core.

```java
Gui<Player, ItemStack> vaultGui = DadaGui.<Player, ItemStack>storage('v')
        .title(player -> "Vault | " + player.getName())
        .layout(
                "# # # # # # # # #",
                "# v v v v v v v #",
                "# v v v v v v v #",
                "# v v v v v v v #",
                "# # # I # C # # #")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('I', ingredients.display(MaterialKey.BOOK, "Info"))
        .ingredient('C', navigation.close())
        .storageProvider(context -> vaultRepository.personal(context.viewer().getUniqueId()))
        .onSave((session, storage) -> vaultRepository.savePersonal(session.viewer().getUniqueId(), storage))
        .scope(GuiScope.PER_PLAYER)
        .build();
```

Il runtime Bukkit protegge automaticamente decorazioni e bottoni, permette movimento item solo negli slot storage, sincronizza la storage prima del refresh e salva su close/quit/shutdown.

### Custom slot behavior

Se un plugin vuole un nuovo tipo di slot, non deve modificare enum o runtime centrale.
Basta aggiungere un comportamento custom:

```java
public final class AuditBehavior implements SlotBehavior<Player, ItemStack> {
    @Override
    public String key() {
        return "my-plugin:audit";
    }

    @Override
    public boolean shouldCancelClick(ClickContext<Player, ItemStack> context) {
        return true;
    }

    @Override
    public void onClick(ClickContext<Player, ItemStack> context) {
        context.viewer().sendMessage("Custom behavior executed");
    }
}
```

E usarlo in una GUI:

```java
.ingredient('A', ingredients.fixed(item, new AuditBehavior()))
```

Il progetto include anche `ExampleSlotBehaviors` nel modulo `dadagui-examples`.

Vedi anche [`docs/USE_CASES.md`](docs/USE_CASES.md) per diagrammi e casi d'uso.

## Esempi inclusi

Il modulo `dadagui-examples` contiene esempi pronti:

```text
/dadagui static    -> GUI statica uguale per tutti
/dadagui player    -> GUI runtime basata sul player
/dadagui shared    -> GUI condivisa tra più player
/dadagui paged     -> GUI paginata stile lista ricette
/dadagui vault     -> vault personale con slot storage
/dadagui teamvault -> vault condiviso tra player
```

### GUI statica

```java
Gui<Player, ItemStack> staticMenu = DadaGui.<Player, ItemStack>staticGui()
        .title("Main Menu")
        .layout(
                "# # # # # # # # #",
                "# A # B # C # X #",
                "# # # # # # # # #")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('A', ingredients.clickable(MaterialKey.DIAMOND, "Profile", click -> {
            click.viewer().sendMessage("Profile clicked");
        }))
        .ingredient('X', navigation.close())
        .scope(GuiScope.STATIC)
        .build();
```

### GUI runtime per player

```java
Gui<Player, ItemStack> playerGui = DadaGui.<Player, ItemStack>staticGui()
        .title(player -> "Profile | " + player.getName())
        .layout(
                "# # # # # # # # #",
                "# N # W # R # X #",
                "# # # # # # # # #")
        .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
        .ingredient('N', ingredients.dynamicSlot(context ->
                GuiSlot.<Player, ItemStack>builder(
                        ingredients.stack(MaterialKey.PLAYER_HEAD, 1, context.viewer().getName()))
                        .build()))
        .ingredient('R', ingredients.clickable(MaterialKey.ARROW, "Refresh", click -> click.refresh()))
        .ingredient('X', navigation.close())
        .scope(GuiScope.PER_PLAYER)
        .build();
```

### GUI condivisa tra player

```java
Gui<Player, ItemStack> sharedGui = DadaGui.<Player, ItemStack>staticGui()
        .title("Shared Counter")
        .layout(
                "# # # # # # # # #",
                "# # # C # R # X #",
                "# # # # # # # # #")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('C', ingredients.dynamicSlot(context ->
                GuiSlot.<Player, ItemStack>builder(
                        ingredients.stack(MaterialKey.GOLD_INGOT, 1, "Clicks: " + sharedClicks.get()))
                        .onClick(click -> {
                            sharedClicks.incrementAndGet();
                            click.refreshAllViewers();
                        })
                        .build()))
        .ingredient('R', ingredients.clickable(MaterialKey.ARROW, "Refresh all", click -> click.refreshAllViewers()))
        .ingredient('X', navigation.close())
        .scope(GuiScope.SHARED)
        .sharedKey("my-plugin:shared-counter")
        .build();
```

---

## Nuovi concetti architetturali

| Concetto | Responsabilità | Modulo |
| --- | --- | --- |
| `DadaGui` | Entry point fluent per creare GUI | `dadagui-api` |
| `GuiLayout` | Parsing e validazione layout a marker | `dadagui-api` |
| `GuiIngredient` | Item/slot renderizzabile, statico o dinamico | `dadagui-api` |
| `PagedLayoutGui` | GUI paginata basata su layout | `dadagui-api` |
| `GuiScope` | Definisce stato statico, per-player o shared | `dadagui-api` |
| `SlotBehavior` | Strategy estendibile che definisce comportamento click/storage di uno slot | `dadagui-api` |
| `SlotBehaviors` | Factory di comportamenti comuni: filler, display, button, content, storage, locked | `dadagui-api` |
| `SlotType` | Solo preset/compatibilità per il builder, non modello runtime centrale | `dadagui-api` |
| `StorageLayoutGui` | GUI per vault/contenitori virtuali con soli slot storage mutabili | `dadagui-api` |
| `PageMode` | Definisce pagina per-player o condivisa | `dadagui-api` |
| `MaterialKey` | Chiave materiale indipendente da Bukkit | `dadagui-api` |
| `BukkitIngredients` | Utility Bukkit per creare ingredienti | `dadagui-bukkit` |
| `BukkitNavigation` | Ingredienti pronti per previous/next/close | `dadagui-bukkit` |

---

## Struttura del progetto

```text
DadaGUI-universal/
├── pom.xml
├── dadagui-api/
├── dadagui-bukkit/
├── dadagui-version-legacy/
├── dadagui-version-modern/
├── dadagui-version-v26_1/
├── dadagui-examples/
└── dadagui-dist-universal/
```

| Modulo | Responsabilità |
| --- | --- |
| `dadagui-api` | API pura del framework. Non dipende da Bukkit, Spigot, Paper o Minecraft. |
| `dadagui-bukkit` | Bridge Bukkit: apre inventory, registra eventi, gestisce sessioni e click. |
| `dadagui-version-legacy` | Adapter per versioni legacy, indicativamente 1.8.x - 1.12.x. |
| `dadagui-version-modern` | Adapter per versioni moderne, indicativamente 1.13.x - 1.21.x. |
| `dadagui-version-v26_1` | Adapter dedicato alla linea Minecraft `26.1.x`. |
| `dadagui-examples` | Plugin demo con comando `/dadagui`. |
| `dadagui-dist-universal` | Modulo che produce il jar finale unico shaded. |

---

## Build

### Requisiti

- Java 8 o superiore per compilare il jar universale.
- Maven 3.8+ consigliato.
- Un server Bukkit/Spigot/Paper per testare il plugin.

### Compilare tutto

```bash
mvn clean package
```

### Compilare solo il jar universale

```bash
mvn clean package -pl dadagui-dist-universal -am
```

### Output

Il jar finale viene generato in:

```text
dadagui-dist-universal/target/DadaGUI-universal-2.8.0-SNAPSHOT.jar
```

Questo è il file da copiare nella cartella:

```text
server/plugins/
```

---

## Avvio demo

1. Compila il progetto:

```bash
mvn clean package -pl dadagui-dist-universal -am
```

2. Copia il jar generato nella cartella `plugins/` del server.

3. Avvia il server.

4. Entra in gioco ed esegui:

```text
/dadagui
```

Il comando apre una GUI paginata dimostrativa.

---

## Perché un solo jar universale usa bytecode Java 8

Il requisito principale del progetto è avere:

```text
un solo plugin jar
+ caricamento automatico su più versioni Minecraft
+ compatibilità anche con server vecchi
```

Per questo motivo tutte le classi incluse nel jar universale devono essere compilate con un bytecode caricabile anche dai server più vecchi.

Un server moderno con Java 17/21 può caricare classi Java 8.  
Un server vecchio con Java 8 non può caricare classi compilate Java 17/21.

Se nel jar universale venisse inclusa anche una sola classe compilata per Java 21, il server potrebbe fallire con:

```text
UnsupportedClassVersionError
```

Per evitare questo problema, la regola del progetto è:

```text
moduli separati nel sorgente
+ un solo jar finale shaded
+ bytecode Java 8
+ adapter automatici
+ reflection/capability checks per API nuove
```

---

## Selezione automatica della versione

Il framework usa `ServiceLoader` per trovare gli adapter disponibili nel jar finale.

Gli adapter implementano:

```java
BukkitVersionAdapter
```

All'avvio `BukkitGuiManager` seleziona automaticamente l'adapter migliore:

```java
BukkitVersionAdapters.bestFor(Bukkit.getServer())
```

Ogni adapter decide se supporta il server corrente:

```java
boolean supports(Server server);
```

Poi il framework usa quello con priorità più alta.

Esempio concettuale:

```text
Server 1.8.x   -> LegacyBukkitVersionAdapter
Server 1.20.x  -> ModernBukkitVersionAdapter
Server 26.1.x  -> Minecraft261BukkitVersionAdapter
Fallback       -> DefaultBukkitVersionAdapter
```

---

## Regole di compatibilità

Per mantenere un solo jar realmente universale:

1. Non usare NMS direttamente nel core.
2. Non importare classi CraftBukkit interne.
3. Non usare API Paper/Folia direttamente nei moduli comuni.
4. Non referenziare direttamente classi presenti solo in versioni recenti.
5. Usare reflection o capability checks negli adapter.
6. Tenere `dadagui-api` completamente indipendente da Bukkit.
7. Tenere `plugin.yml` senza `api-version` se si vuole supportare anche server legacy.

---

## Aggiungere un nuovo adapter Minecraft

Esempio: supporto dedicato a `26.2.x`.

### 1. Crea il modulo

```text
dadagui-version-v26_2/
```

### 2. Fallo dipendere da `dadagui-bukkit`

Nel `pom.xml` del modulo:

```xml
<dependency>
    <groupId>me.mrbast</groupId>
    <artifactId>dadagui-bukkit</artifactId>
</dependency>
```

### 3. Implementa l'adapter

```java
public final class Minecraft262BukkitVersionAdapter implements BukkitVersionAdapter {
    @Override
    public String id() {
        return "minecraft-26.2";
    }

    @Override
    public int priority() {
        return 300;
    }

    @Override
    public boolean supports(Server server) {
        return server.getBukkitVersion().startsWith("26.2");
    }
}
```

### 4. Registra l'adapter con ServiceLoader

Crea il file:

```text
src/main/resources/META-INF/services/me.mrbast.dadagui.bukkit.version.BukkitVersionAdapter
```

Contenuto:

```text
me.mrbast.dadagui.version.v26_2.Minecraft262BukkitVersionAdapter
```

### 5. Aggiungi il modulo nel parent `pom.xml`

```xml
<module>dadagui-version-v26_2</module>
```

### 6. Aggiungilo al dist universale

Nel modulo `dadagui-dist-universal`:

```xml
<dependency>
    <groupId>me.mrbast</groupId>
    <artifactId>dadagui-version-v26_2</artifactId>
</dependency>
```

---

## Usare DadaGUI in un tuo plugin

Finché il progetto non è pubblicato su Maven Central o su un repository Maven privato, puoi installarlo localmente:

```bash
mvn clean install
```

Poi, nel tuo plugin, puoi dipendere dai moduli principali:

```xml
<dependency>
    <groupId>me.mrbast</groupId>
    <artifactId>dadagui-api</artifactId>
    <version>2.8.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>me.mrbast</groupId>
    <artifactId>dadagui-bukkit</artifactId>
    <version>2.8.0-SNAPSHOT</version>
</dependency>
```

Se vuoi includere DadaGUI dentro il tuo plugin finale, configura Maven Shade nel tuo plugin.

---

## Esempio plugin

```java
public final class MyPlugin extends JavaPlugin {
    private BukkitGuiManager guiManager;

    @Override
    public void onEnable() {
        this.guiManager = new BukkitGuiManager(this);
        this.guiManager.register();
    }

    @Override
    public void onDisable() {
        if (guiManager != null) {
            guiManager.unregister();
        }
    }

    public void openMenu(Player player) {
        Gui<Player, ItemStack> gui = StaticGui.<Player, ItemStack>builder("Menu", 27)
                .slot(13, GuiSlot.<Player, ItemStack>builder(new ItemStack(Material.DIAMOND))
                        .onClick(ctx -> ctx.viewer().sendMessage("Diamante cliccato"))
                        .build())
                .build();

        guiManager.open(player, gui);
    }
}
```

---

## Filosofia architetturale

DadaGUI segue una separazione netta:

```text
API framework
  ↓
runtime Bukkit
  ↓
adapter versione
  ↓
jar finale universale
```

Il core non deve sapere:

- su quale versione Minecraft gira il server;
- se il server è Bukkit, Spigot o Paper;
- come sono gestiti i materiali legacy;
- quali API nuove sono disponibili;
- quali fallback servono per versioni vecchie.

Queste responsabilità stanno nei moduli runtime e version adapter.

---

## Roadmap consigliata

- [ ] Testare il jar su server legacy 1.8.x / 1.12.x.
- [ ] Testare il jar su server moderni 1.16.x / 1.20.x / 1.21.x.
- [ ] Testare il jar sulla linea `26.1.x`.
- [ ] Aggiungere esempi per shop GUI.
- [ ] Aggiungere esempi per conferma sì/no.
- [ ] Aggiungere esempi per GUI configurate da file YAML/JSON.
- [ ] Aggiungere supporto layout dichiarativi.
- [ ] Aggiungere placeholder runtime.
- [ ] Aggiungere test automatici sugli adapter.
- [ ] Aggiungere GitHub Actions per build Maven.
- [ ] Valutare supporto Paper/Folia tramite capability module opzionale.

---

## Convenzioni di sviluppo

- Le API pubbliche devono essere semplici e documentate.
- I moduli comuni devono restare indipendenti dalle versioni Minecraft.
- Le classi nuove devono avere responsabilità piccole e chiare.
- Gli slot devono essere oggetti riutilizzabili.
- Le GUI non devono gestire direttamente eventi Bukkit.
- Il refresh deve passare dalla sessione.
- Gli adapter devono contenere solo logica di compatibilità.
- Le API moderne vanno usate con fallback sicuro.

---

## Licenza

Aggiungere una licenza prima della pubblicazione ufficiale del framework.

Consigliata:

```text
MIT
```

oppure:

```text
Apache-2.0
```

se vuoi una licenza più strutturata per uso framework/libreria.

## Example gallery

The `dadagui-examples` module now contains a wider set of ready-to-copy examples:

```text
/dadagui hub          -> example hub
/dadagui static       -> static reusable GUI
/dadagui confirm      -> confirmation dialog
/dadagui player       -> runtime per-player settings/profile GUI
/dadagui shared       -> shared counter GUI
/dadagui vote         -> shared vote board
/dadagui paged        -> personal paged recipe list
/dadagui online       -> shared paged online-player list
/dadagui shop         -> shop category menu
/dadagui shopblocks   -> paged shop category
/dadagui shoprare     -> paged shop category
/dadagui shoputility  -> paged shop category
/dadagui vault        -> personal storage vault
/dadagui teamvault    -> shared/team storage vault
```

See [`docs/EXAMPLES.md`](docs/EXAMPLES.md) for the full explanation and code snippets.

---

## Shading DadaGUI inside another plugin

Use `dadagui-bundle-universal`, not `dadagui-dist-universal`.

```bash
mvn clean install -pl dadagui-bundle-universal -am
```

Then in your plugin:

```xml
<dependency>
    <groupId>it.dadagui</groupId>
    <artifactId>dadagui-bundle-universal</artifactId>
    <version>2.8.0-SNAPSHOT</version>
</dependency>
```

Then configure `maven-shade-plugin` with `ServicesResourceTransformer`, because the Minecraft version adapters are loaded using `ServiceLoader`.

Full guide: [`docs/SHADING.md`](docs/SHADING.md).

---

## API item-oriented: `GuiEntry`

Dalla versione `2.8.0-SNAPSHOT` DadaGUI supporta anche uno stile più vicino ai sistemi basati su `AbstractItem`.
È utile quando ogni contenuto deve sapere da solo:

- come disegnarsi;
- quale item mostrare;
- cosa fare al click.

Invece di mutare la GUI globale con `gui.addContent(...)`, passi gli entry **solo nella richiesta di apertura**.

```java
PagedEntryGui<Player, ItemStack> recipeGui = DadaGui.<Player, ItemStack>pagedEntries('x')
        .title("Pick The Recipe To Craft")
        .layout(
                "# # # # # # # # #",
                "# x x x x x x x #",
                "# # # < # > # # C")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('<', navigation.previousPage())
        .ingredient('>', navigation.nextPage())
        .ingredient('C', navigation.close())
        .emptyIngredient(ingredients.display(MaterialKey.GRAY_STAINED_GLASS_PANE, " "))
        .scope(GuiScope.PER_PLAYER)
        .pageMode(PageMode.PER_PLAYER)
        .build();
```

Quando devi aprirla:

```java
recipeGui.open(player)
        .entries(validRecipes, (recipe, index) -> new RecipeEntry(context, recipe, ingredients))
        .onClose(session -> crafting.finishBusy())
        .show(guiManager);
```

Esempio di entry:

```java
public final class RecipeEntry implements GuiEntry<Player, ItemStack> {
    private final Context context;
    private final Recipe recipe;
    private final BukkitIngredients ingredients;

    public RecipeEntry(Context context, Recipe recipe, BukkitIngredients ingredients) {
        this.context = context;
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    @Override
    public GuiSlot<Player, ItemStack> toSlot(GuiRenderContext<Player, ItemStack> renderContext, int slotIndex) {
        ItemStack item = recipe.getGUIItem() == null
                ? defaultRecipeItem()
                : recipe.getGUIItem().getBuilder().build();

        return GuiSlot.<Player, ItemStack>builder(item)
                .button()
                .onClick(click -> {
                    context.getCrafting().craft(context, recipe);
                    click.close();
                })
                .build();
    }
}
```

Questo modello mantiene la GUI riutilizzabile e isola i dati runtime nella sessione.

```text
GUI template       -> riutilizzabile
Open request       -> entries, attribute, onClose specifici
GuiSession         -> stato di quella apertura
GuiEntry           -> item-oriented render + click
```

Comandi demo aggiunti:

```text
/dadagui entryrecipes -> ricette item-oriented tipo vecchio RecipeItem
/dadagui entryshop    -> shop rapido con GuiEntry
```

Documentazione dedicata: [`docs/ENTRY_API.md`](docs/ENTRY_API.md).
