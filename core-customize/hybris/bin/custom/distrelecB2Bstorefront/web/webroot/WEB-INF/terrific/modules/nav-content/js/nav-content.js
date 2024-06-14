(function($) {

	/**
	 * Category Navigation as Accordion.
	 *
	 * For selection of categories e.g. Newsletter, Support and Services, Information Center
	 * - Add class 'is-expanded' to a .accordion-item to make an item open on page load
	 * - Add class .active to set a menu option as having been selected
	 *
	 * @namespace Tc.Module
	 * @class Nav My Account Content
	 * @extends Tc.Module
	 */
	Tc.Module.NavContent = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			this.$accordionItems = $ctx.find('.accordion-item');
			this.$indicatorIcons = this.$accordionItems.find('a > i');
			this.$springs = $ctx.find('.spring');
			this.$expandedAccordionItem = $('.is-expanded', this.$accordionItems.parent());
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

			// Initially open a zabedee on page load
			//self.closeAccordionItems();
			//this.toggleAccordionItem(this.$expandedAccordionItem);

			self.$accordionItems.on(
					'click.NavMyaccountContent',
					function (evt){

						if ($(evt.target).hasClass('menuOption')) return;

						var  $zebedee = $(this)
							,$currentSpring = $zebedee.find('.spring')[0]
							,$alreadyUncoiledSpring = $.grep(
														$(self.$accordionItems).find('.spring'),
															function(ele,index){
																return $(ele).is(":visible");
															})[0];
						if ( ! $($currentSpring).is($alreadyUncoiledSpring)) {
							self.closeAccordionItems();
							self.toggleAccordionItem($zebedee);
						}
						else {
							self.toggleAccordionItem($zebedee);
						}
					});

			callback();
		},

		closeAccordionItems: function() {
			this.$springs.slideUp("fast").find('.badge').hide();
			this.$accordionItems.removeClass('is-expanded').children('.badge').css({left:0,top:0}).show();
		},

		toggleAccordionItem: function($accordionItem){

			var $spring = $('.spring', $accordionItem),
				$parent = $spring.parent();

			$spring.slideToggle(function() {
				$parent.toggleClass('is-expanded');
				if($parent.hasClass('is-expanded')) {
					var x=0, y=90;
					$parent.children('.badge').show().animate({left:x,top:y},200,function(){$parent.children('.badge').hide();$spring.find('.badge').show();});
				} else {
					$parent.children('.badge').css({left:0,top:0}).show();
					$spring.find('.badge').hide();
				}
			});

		}

	});

})(Tc.$);
