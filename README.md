# Discord Pokemon API Bot

This is a Discord bot coded in Java using the [Javacord](https://github.com/Javacord/Javacord) library. All data is being requested from [pokeapi.co](https://pokeapi.co/).

# Functionality

Whenever the `!pokemon` command is used followed by either the Pokemon name or ID, it will return that Pokemon's stats and also recommend Natures based on it's stat spread.

# Examples

`!pokemon slowpoke`

**Name**: slowpoke

**ID**: 79

**Speed**: 15

**Special Defence**: 40

**Special Attack**: 40

**Defence**: 65

**Attack**: 65

**HP**: 90

**Base Stat Total**: 315.0

**Optimized Stat Total**: 275.0 *(minus the unused attack stat)*

**Highest Stat**: Defence

**Recommended Nature**: Impish (+Def/-Sp.Atk)

**Secondary Option**: Adamant (+Atk/-Sp.Atk)
