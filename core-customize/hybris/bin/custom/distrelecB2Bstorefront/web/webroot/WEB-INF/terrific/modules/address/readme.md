How to Use Addres and Address List Modules.

Address Module:
How to call Address Module with default settings:

mod:address template="billing-b2b" address="${address}" />

Templates:
- billing-b2b
- billing-b2c
- pickup
- shipping-b2b
- shipping-b2c

There are the following different Actions on addresses, which are needed throughout the shop:

- Edit Button
- Select Button
- Change Button
- Select Radio (Address List)

To show one of these action elements with the address, call the address Module with the parameter addressActionMode and one of the following values:

- edit
- select
- change
- radio

Note: The different Addresses only contain the actions, which are allowed from spec.

Address List Module:
The address-list module needs to be called with the following params:

<mod:address-list
	skin="billing"
	addressList="${billingAddresses}"
	addressType="billing"
	customerType="b2b"
	selectedAddressId="${cartData.billingAddress.id}"
	addressActionMode="radio"
/>

Note: The skins are only for JS Reasons and the JS is specific for the Checkout Address Page. If you need additional
Behaviour on different pages, create a new skin!

So for example if you need an Address List with radion buttons, the addressActionMode param is needed and will be passed
through to the address module. The SelectedAddressId is needed to determine which address should be checked.