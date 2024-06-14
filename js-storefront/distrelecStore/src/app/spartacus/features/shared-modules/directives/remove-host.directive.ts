import { Directive, ElementRef, OnInit } from '@angular/core';

@Directive({
  selector: '[removeHost]',
})
export class RemoveHostDirective implements OnInit {
  constructor(private el: ElementRef) {}

  /**
   * wait for the component to render completely
   */
  ngOnInit(): void {
    // move all children out of the element
    const nativeElement: HTMLElement = this.el.nativeElement;
    const parentElement: HTMLElement = nativeElement.parentElement;

    // Check if a parent element exists
    // Prevents JS Errors:
    //   1. "Cannot read property 'insertBefore' of null"
    //   2. "Cannot read property 'removeChild' of null"
    if (parentElement) {
      while (nativeElement.firstChild) {
        parentElement.insertBefore(nativeElement.firstChild, nativeElement);
      }
      // remove the empty element(the host)
      parentElement.removeChild(nativeElement);
    }
  }
}
