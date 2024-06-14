# MetaHdSuggest

The module that implements auto-suggest results when typing in search.

## Basics

- Actual rendering of the auto-suggest content is triggered by events from metahd-item-search
- All data is provided by metahd-item-search via event data
- This module is passive and never sends/triggers/calls anything back.
- The autosuggest content is rendered in the browser with doT.js from data and templates in the jsp.
- For each "result row" a cart-logic module is instanced. The necessary data is provided to cart-logic via attributes
  on the quantity input field.
- Twitter Bootstrap Tabs are used in the UI. The necessary library CSS has been stripped and customized, not overridden.

### Templating and Rendering

- The autosuggest UI is re-rendered from scratch each time and discarded when not shown anymore.
- First the UI excluding result rows are rendered, then the result rows are rendered into that. (Without doT partials)