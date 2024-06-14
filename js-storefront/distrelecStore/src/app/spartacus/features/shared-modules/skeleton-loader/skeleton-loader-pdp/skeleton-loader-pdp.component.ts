import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-skeleton-loader-pdp',
  templateUrl: './skeleton-loader-pdp.component.html',
})
export class SkeletonLoaderPDPComponent implements OnInit {
  arrayOf3: number[] = [];
  arrayOf4: number[] = [];
  arrayOf5: number[] = [];
  arrayOf7: number[] = [];
  arrayOf8: number[] = [];
  arrayOf11: number[] = [];

  constructor() {}

  ngOnInit(): void {
    this.arrayOf3 = this.createRange(3);
    this.arrayOf4 = this.createRange(4);
    this.arrayOf5 = this.createRange(5);
    this.arrayOf7 = this.createRange(7);
    this.arrayOf8 = this.createRange(8);
    this.arrayOf11 = this.createRange(11);
  }

  createRange(quantity: number): number[] {
    return new Array(quantity).fill(0).map((n, index) => index + 1);
  }
}
