# Engination Enginogrammetry
Industrialism and Automation for Lazor Enthusiasts

#License: MIT
See LICENSE for details. Go ahead and add this mod to your modpack!
No need to notify me, though I might pop in and say hello if you do! ^_^

# Cloning and Compiling
In addition to the files in this repository you'll need to copy-in the
following API packages:

* CapabilitiesCore https://github.com/gigaherz/CapabilityCore
* COFH's RedstoneFlux-API, unofficially updated
    https://github.com/Parker8283/RedstoneFlux-API

# Contributing
* Tabs for indentation
* Spaces for cosmetic alignment between elements indented to the same level
* If it doesn't line up right when you change your tab spacing, you did it wrong
* License information at the top of every java source file, above the package declaration.
* Squash your commits before you submit a PR (i.e. there should be only one commit per PR)


The following kinds of pull request will automatically be denied:
* Formatting
* Addition or maintenance of legacy or extremely obscure APIs
* Code containing known bugs, or code which can't compile as presented

# Making Friends
If you want your automation to be as friendly as possible to this mod,
and interact as quickly as possible, use Capabilities. POJO interface
APIs like the official-RF are just passthrough methods to help legacy
mods hang on until their authors adapt. Support for these legacy APIs
will eventually be dropped! On the other hand, support for additional
well-envisioned energy APIs could happen in the near future. (I'm
looking at you, Tesla!)


RF cables in this mod have a minimum granularity. On the one hand, this
allows them to be extremely light-weight with no TileEntities at all,
but on the other hand, you can't dribble in 1RF at a time. In order to
speak fluently with Engination, I'd recommend machines have AT LEAST
30RF of internal storage or buffer, in order to accept the minimum -
sized packets of energy.


# Balancing against this mod
Yes, this mod actually has balance rules. Yes, you still have
permission to include the mod in a horribly broken modpack that ignores
all these rules. I just won't certify it as fair and balanced.
* No ore tripling, quadrupling, quintupling. Ever. At most, on average, each iron ore can yield 1.6 ingots of iron ingot.
* A single block can store no more than 36 items - that is, a single-chest with an extra row.
* Wireless interactions are premitted, and in fact, encouraged. Most wireless is good for servers.
* Time is not a balancing mechanic. Free energy that takes a long time to generate is still free energy.
* Material cost is not a balancing mechanic. An overpowered machine that requires 26 expensive casing blocks is overpowered.

These last two bullet points are to prevent first-order-optimizations.
Vazkii has some really good writing on passive generation and why you want
to avoid it (http://vazkii.us/uncategorized/sins-of-a-solar-empire-or-the-passive-generation-conundrum/ ),
and Extra Credits has a whole video on FOO ( https://www.youtube.com/watch?v=EitZRLt2G3w ).
This is really important, and I feel like we aren't getting it as modpack
creators. Please think about these things. They matter.
