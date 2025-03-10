# @javed-shahzad/capacitor-speacker-phone-toggle

Capacitor plugin to audio toggle

## Install

```bash
npm install @javed-shahzad/capacitor-speacker-phone-toggle
npx cap sync
```

## API

<docgen-index>

* [`setSpeakerOn(...)`](#setspeakeron)
* [`reset()`](#reset)
* [`addListener('speakerOn', ...)`](#addlistenerspeakeron-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
### Import the plugin: In your code, import the plugin:

```
 import { AudioTogglePlugin } from '@javed-shahzad/capacitor-speacker-phone-toggle';
```

### setSpeakerOn(...)

```typescript
setSpeakerOn(data: { speakerOn: boolean; }) => Promise<void>
```

| Param      | Type                                 |
| ---------- | ------------------------------------ |
| **`data`** | <code>{ speakerOn: boolean; }</code> |

--------------------


### reset()

```typescript
reset() => Promise<void>
```

--------------------


### addListener('speakerOn', ...)

```typescript
addListener(eventName: 'speakerOn', listenerFunc: (data: { status: boolean; }) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                 |
| ------------------ | ---------------------------------------------------- |
| **`eventName`**    | <code>'speakerOn'</code>                             |
| **`listenerFunc`** | <code>(data: { status: boolean; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
