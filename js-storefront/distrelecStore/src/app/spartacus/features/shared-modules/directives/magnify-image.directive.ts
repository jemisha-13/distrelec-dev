import { isPlatformBrowser } from '@angular/common';
import { Directive, ElementRef, HostListener, Inject, Input, OnDestroy, PLATFORM_ID } from '@angular/core';
import { WindowRef } from '@spartacus/core';
import { DistBreakpointService } from '@services/breakpoint.service';
import { Subscription } from 'rxjs';

const lensStyle = `position: absolute;
border: 1px solid grey;
border-radius: 50%;
overflow: hidden;
cursor:none;
box-shadow: inset 0 0 10px 2px grey;
filter: drop-shadow(0 0 2px grey);
width: 150px;
height: 150px;
z-index: 1;
`;

// Lens animations
const lensInitAnimationScale = [{ transform: 'rotate(0) scale(0)' }, { transform: 'rotate(0) scale(1)' }];

const lensAnimationScaleTiming = {
  duration: 250,
  iterations: 1,
};

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[magnifyImage]',
})
export class MagnifyImageDirective implements OnDestroy {
  @Input('magnifyImage') imageSource: string;

  imgElement;
  lensElement: HTMLElement;
  width: number;
  height: number;
  bw: number;
  zoom = 3;

  private isLessThanDesktop$ = this.breakpointService.isMobileOrTabletBreakpoint();
  private subscription = new Subscription();

  constructor(
    @Inject(PLATFORM_ID) private platformId: any,
    private el: ElementRef,
    private winRef: WindowRef,
    private breakpointService: DistBreakpointService,
  ) {}

  @HostListener('load')
  onLoad() {
    if (isPlatformBrowser(this.platformId)) {
      this.magnify();
    }
  }

  magnify() {
    this.imgElement = this.el.nativeElement;
    // Create magnifier lens:
    this.lensElement = document.createElement('DIV');

    this.lensElement.setAttribute('style', lensStyle);
    // Before adding new child node, need to remove the div elements from other images to prevent child node stacking
    this.imgElement.parentElement.childNodes.forEach((node) => {
      if (node.nodeName === 'DIV') {
        this.imgElement.parentElement.removeChild(node);
      }
    });
    // Insert magnifier lens:
    this.imgElement.parentElement.insertBefore(this.lensElement, this.imgElement);
    // Set background properties for the magnifier lens:
    this.lensElement.style.backgroundImage = "url('" + this.imageSource + "')";
    this.lensElement.style.backgroundRepeat = 'no-repeat';
    this.lensElement.style.backgroundSize =
      this.imgElement.width * this.zoom + 'px ' + this.imgElement.height * this.zoom + 'px';
    this.bw = 3;

    // this.lensElement.offsetWidth / 2
    this.width = 75;
    // this.lensElement.offsetHeight / 2
    this.height = 75;
    // Hide the lensElement on initial
    this.lensElement.style.display = 'none';

    // Execute a function when someone moves the magnifier lens over the image:
    this.lensElement.addEventListener('mousemove', (e) => this.moveMagnifier(e));
    this.imgElement.addEventListener('mousemove', (e) => this.moveMagnifier(e));
    this.lensElement.addEventListener('touchmove', (e) => this.moveMagnifier(e));
    this.imgElement.addEventListener('touchmove', (e) => this.moveMagnifier(e));

    // When we enter inside the img there will be no lens, so We are adding the mouseenter event listener to the img element
    this.imgElement.addEventListener('mouseenter', (e) => this.magnifierActive(e, true));
    // On mouse out, our mouse is on the lens so it will trigger through lens element
    this.imgElement.parentElement.addEventListener('mouseleave', (e) => this.magnifierActive(e, false));

    //On resize we need to update the image width and height
    const imgResizeObserver = new ResizeObserver((entries) => {
      for (const entry of entries) {
        const cr = entry.contentRect;
        this.lensElement.style.backgroundSize = cr.width * this.zoom + 'px ' + cr.height * this.zoom + 'px';
      }
    });

    imgResizeObserver.observe(this.imgElement);
  }

  magnifierActive(event, isActive) {
    this.subscription.add(
      this.isLessThanDesktop$.subscribe((isLessThenDesktop) => {
        //if lessThenDesktop is true, then we need to hide the lens
        if (isLessThenDesktop) {
          isActive = false;
        }
      }),
    );

    if (isActive) {
      this.lensElement.style.display = 'block';
      this.imgElement.style.opacity = '0.7';
      this.lensElement.animate(lensInitAnimationScale, lensAnimationScaleTiming);
    } else {
      this.lensElement.style.display = 'none';
      this.imgElement.style.opacity = '1';
    }
  }
  moveMagnifier(event) {
    let x;
    let y;
    // Prevent any other actions that may occur when moving over the image
    event.preventDefault();
    // Get the cursor's x and y positions:
    const pos = this.getCursorPos(event);
    x = pos.posX;
    y = pos.posY;
    // Prevent the magnifier lens from being positioned outside the image:
    if (x > this.imgElement.width - this.width / this.zoom) {
      x = this.imgElement.width - this.width / this.zoom;
    }
    if (x < this.width / this.zoom) {
      x = this.width / this.zoom;
    }
    if (y > this.imgElement.height - this.height / this.zoom) {
      y = this.imgElement.height - this.height / this.zoom;
    }
    if (y < this.height / this.zoom) {
      y = this.height / this.zoom;
    }
    // Set the position of the magnifier lens:
    this.lensElement.style.left = x - this.width + 'px';
    this.lensElement.style.top = y - this.height + 'px';
    // Display what the magnifier lens "sees":
    this.lensElement.style.backgroundPosition =
      '-' + (x * this.zoom - this.width + this.bw) + 'px -' + (y * this.zoom - this.height + this.bw) + 'px';
  }

  getCursorPos(event) {
    let posX = 0;
    let posY = 0;
    event = event || this.winRef.nativeWindow.event;
    // Get the x and y positions of the image:
    const a = this.imgElement.getBoundingClientRect();
    // Calculate the cursor's x and y coordinates, relative to the image:
    posX = event.pageX - a.left;
    posY = event.pageY - a.top;
    // Consider any page scrolling:
    posX = posX - this.winRef.nativeWindow.scrollX;
    posY = posY - this.winRef.nativeWindow.scrollY;
    return { posX, posY };
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
