Thanks for your interest in maintaining a Quark addon into 1.20. Please pardon our dust and let us know if there's something we missed.

## Repackaging

The source code has moved under the `org.violetmoon` organization.

## all this "zeta" stuff?

Zeta is a platform-agnostic modding platform. We are working on a Forge implementation of Zeta, and we are working on implementing Quark in terms of Zeta in order to achieve our goal of creating a Fabric port of Quark.

Zeta aims to be a big framework for writing configurable mods. It ships with a config system and config GUI system, and its event bus is designed with configurability in mind (enabling/disabling modules is baked into the "load bus"/"play bus" distinction). Most of the useful stuff that was in AutoRegLib has moved to Zeta, too.

Zeta is big, but intentionally avoids as much `static` as possible as a way of supporting multiple mods using the library simultaneously. You will not find many singletons in Zeta, and each mod is expected to create and manage their own instances of it. Quark's are under `Quark.ZETA` and `QuarkClient.ZETA_CLIENT`.

## Constructing Quark's blocks

Much like in the AutoRegLib days, Zeta leverages constructor registration for its blocks and items. This is now much more closely tied to the Zeta systems, but you can now pass `null` for any registry-name/module parameters, and Zeta just won't try to register them.

## Dealing with events

If Quark moves away from Forge, obviously we can't use its event bus. Most of the relevant Forge events have been reimplemented.

Events fired into Zeta mods are handled with code at the bottom of `ForgeZeta` that temporarily translates them into Zeta's event system.

If you want to listen to an event fired *from* a Zeta mod, you have two options:

* Engage with the Zeta event bus. You can find this in `Quark.ZETA.loadBus`/`playBus`.
  * The event bus itself is very basic and doesn't require you to write "a Zeta mod". 
  * Zeta's `@LoadEvent` and `@PlayEvent` are the analogs of Forge's `@SubscribeEvent`.
  * Subscribing to the event bus works the same as Forge, where passing a `.class` subscribes static methods, and passing an instance of something subscribes non-static methods.
* Certain events get also fired as platform-specific events. See `ForgeZeta.fireExternalEvent`.
  * Usually events corresponding to a "public api" go here. 
  * Listen to these events using the regular platform-specific event bus system.