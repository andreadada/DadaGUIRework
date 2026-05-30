# DadaGUI Examples

This module contains ready-to-copy examples showing how to use the framework without creating giant services or tightly coupling GUI code to Bukkit internals.

The examples are intentionally split by use case:

```text
DadaGuiExamplePlugin.java      -> command wiring and GUI registry
ExampleStaticGuis.java         -> static hub + confirmation dialog
ExamplePlayerRuntimeGuis.java  -> per-player runtime settings/profile GUI
ExampleSharedGuis.java         -> shared counter + shared vote board
ExamplePagedGuis.java          -> personal paged recipes + shared paged online players
ExampleShopGuis.java           -> shop home + paged category + confirmation dialog
ExampleVaultGuis.java          -> personal vault + shared/team vault
ExampleShopCatalog.java        -> tiny replaceable catalog/balance store
ExampleVaultRepository.java    -> tiny replaceable in-memory vault store
```

## Commands

```text
/dadagui hub          -> opens the main example hub
/dadagui static       -> static reusable GUI
/dadagui confirm      -> static confirmation dialog
/dadagui player       -> per-player runtime GUI
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

## Static GUI

Static GUIs are built once and reused for every player. The menu definition is stable, while click callbacks can open other views.

```java
Gui<Player, ItemStack> hub = DadaGui.<Player, ItemStack>staticGui()
        .title("DadaGUI | Examples")
        .layout(
                "# # # # # # # # #",
                "# S P H V M # # #",
                "# A # # # # # X #",
                "# # # # # # # # #")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('P', ingredients.clickable(MaterialKey.PLAYER_HEAD, "Player runtime", click ->
                guiManager.open(click.viewer(), playerSettings)))
        .ingredient('X', navigation.close())
        .scope(GuiScope.STATIC)
        .build();
```

## Per-player runtime GUI

Use one definition and render values from the current viewer/session.

```java
Gui<Player, ItemStack> settings = DadaGui.<Player, ItemStack>staticGui()
        .title(player -> "Settings | " + player.getName())
        .layout(
                "# # # # # # # # #",
                "# N # W # B # # #",
                "# T # S # R # X #",
                "# # # # # # # # #")
        .ingredient('T', ingredients.dynamicSlot(context -> {
            boolean enabled = settingsStore.notifications(context.viewer().getUniqueId());
            return GuiSlot.<Player, ItemStack>builder(ingredients.stack(
                            enabled ? MaterialKey.LIME_STAINED_GLASS_PANE : MaterialKey.RED_STAINED_GLASS_PANE,
                            1,
                            "Notifications: " + enabled))
                    .button()
                    .onClick(click -> {
                        settingsStore.toggleNotifications(click.viewer().getUniqueId());
                        click.refresh();
                    })
                    .build();
        }))
        .scope(GuiScope.PER_PLAYER)
        .build();
```

## Shared GUI

Shared GUIs use one shared key and can refresh all viewers together.

```java
Gui<Player, ItemStack> sharedCounter = DadaGui.<Player, ItemStack>staticGui()
        .title("Shared Counter")
        .layout(
                "# # # # # # # # #",
                "# # # C # R # X #",
                "# # # # # # # # #")
        .ingredient('C', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(counterItem())
                .button()
                .onClick(click -> {
                    counter.incrementAndGet();
                    click.refreshAllViewers();
                })
                .build()))
        .scope(GuiScope.SHARED)
        .sharedKey("examples:shared-counter")
        .build();
```

## Paged GUI

Paged GUIs reserve a content marker, then render data items into those slots.

```java
Gui<Player, ItemStack> recipes = DadaGui.<Player, ItemStack, String>paginated('x')
        .title("Recipes")
        .layout(
                "# # # # # # # # #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # < # > # # C")
        .ingredient('<', navigation.previousPage())
        .ingredient('>', navigation.nextPage())
        .ingredient('C', navigation.close())
        .contentProvider(context -> recipesFor(context.viewer()))
        .contentRenderer((context, recipe, index) -> ingredients.clickable(
                MaterialKey.CRAFTING_TABLE,
                recipe,
                click -> click.viewer().sendMessage("Selected " + recipe)))
        .scope(GuiScope.PER_PLAYER)
        .pageMode(PageMode.PER_PLAYER)
        .build();
```

## Shop

The shop example composes three small views:

```text
shopHome()             -> static category menu
shopCategory(category) -> paged item list
confirmPurchase(...)   -> confirmation dialog
```

The GUI depends on a tiny `ExampleShopCatalog`, which can be replaced by your real economy/catalog service later.

## Vault

Storage GUIs reserve one marker for mutable slots. Every other slot remains protected.

```java
Gui<Player, ItemStack> vault = DadaGui.<Player, ItemStack>storage('v')
        .title(player -> "Vault | " + player.getName())
        .layout(
                "# # # # # # # # #",
                "# v v v v v v v #",
                "# v v v v v v v #",
                "# v v v v v v v #",
                "# # # I # C # # #")
        .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
        .ingredient('C', navigation.close())
        .storageProvider(context -> vaultRepository.personal(context.viewer().getUniqueId()))
        .onSave((session, storage) -> vaultRepository.savePersonal(session.viewer().getUniqueId(), storage))
        .scope(GuiScope.PER_PLAYER)
        .build();
```

For a team vault use the same builder with:

```java
.scope(GuiScope.SHARED)
.sharedKey("my-plugin:team-vault:" + teamId)
.storageProvider(context -> vaultRepository.shared(teamId))
```

## Design notes

The examples follow the same design rule:

```text
GUI classes define layout and interaction.
Small stores/repositories own state.
Bukkit runtime only renders and dispatches events.
```

That keeps the framework low-coupled and cohesive while still being easy to use.

---

## Item-oriented entries

Comandi demo:

```text
/dadagui entryrecipes
/dadagui entryshop
```

### Ricette stile `RecipeItem`

```java
recipePicker.open(player)
        .entries(validRecipes, (recipe, index) -> new RecipeEntry(craftingContext, recipe, ingredients))
        .onClose(session -> craftingContext.finishBusy())
        .show(guiManager);
```

Ogni entry implementa:

```java
GuiSlot<Player, ItemStack> toSlot(GuiRenderContext<Player, ItemStack> context, int slotIndex)
```

Questa API è più pratica quando l'oggetto deve decidere da solo item, nome, lore e click.

### Shop entry rapida

```java
quickShop.open(player)
        .entries(items, (item, index) -> ingredients.entry(
                item,
                value -> ingredients.stack(value.icon(), 1, value.displayName()),
                (click, value) -> click.viewer().sendMessage("Bought " + value.displayName())))
        .show(guiManager);
```
