import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-shopping-list-sort-by',
  templateUrl: './sort-by.component.html',
  styleUrls: ['./sort-by.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ShoppingListSortByComponent implements OnInit {
  @Input() entries;
  @Output() sortByClick = new EventEmitter();

  selectModelValue: number;

  constructor() {}

  onChange(event): void {
    this.selectModelValue = +event;

    switch (+event) {
      case 0:
        this.dateAsc();
        break;
      case 1:
        this.dateDesc();
        break;
      case 2:
        this.nameAsc();
        break;
      case 3:
        this.nameDesc();
        break;
      case 4:
        this.artNrAsc();
        break;
      case 5:
        this.artNrDesc();
        break;
      case 6:
        this.priceAsc();
        break;
      case 7:
        this.priceDesc();
        break;
      case 8:
        this.manufacturerAsc();
        break;
      case 9:
        this.manufacturerDesc();
        break;
    }
  }

  dateAsc(): void {
    this.entries.sort(
      (a, b) =>
        a?.addedDate.replace(/-/g, '').replace(/:/g, '').replace('+', '').replace(/T/g, '') -
        b?.addedDate.replace(/-/g, '').replace(/:/g, '').replace('+', '').replace(/T/g, ''),
    );
    this.sortByClick.emit(this.entries);
  }

  dateDesc(): void {
    this.entries.sort(
      (a, b) =>
        b?.addedDate.replace(/-/g, '').replace(/:/g, '').replace('+', '').replace(/T/g, '') -
        a?.addedDate.replace(/-/g, '').replace(/:/g, '').replace('+', '').replace(/T/g, ''),
    );

    this.sortByClick.emit(this.entries);
  }

  nameAsc(): void {
    this.entries.sort((a, b) => a?.product?.name.localeCompare(b?.product?.name));

    this.sortByClick.emit(this.entries);
  }

  nameDesc(): void {
    this.entries.sort((a, b) => b?.product?.name.localeCompare(a?.product?.name));

    this.sortByClick.emit(this.entries);
  }

  artNrAsc(): void {
    this.entries.sort((a, b) => a?.product?.code - b?.product?.code);

    this.sortByClick.emit(this.entries);
  }

  artNrDesc(): void {
    this.entries.sort((a, b) => b?.product?.code - a?.product?.code);

    this.sortByClick.emit(this.entries);
  }

  priceAsc(): void {
    this.entries.sort((a, b) => a?.priceObject?.price?.value - b?.priceObject?.price?.value);
    this.sortByClick.emit(this.entries);
  }

  priceDesc(): void {
    this.entries.sort((a, b) => b?.priceObject?.price?.value - a?.priceObject?.price?.value);
    this.sortByClick.emit(this.entries);
  }

  manufacturerAsc(): void {
    this.entries.sort((a, b) => a?.product?.distManufacturer?.name.localeCompare(b?.product?.distManufacturer?.name));
    this.sortByClick.emit(this.entries);
  }

  manufacturerDesc(): void {
    this.entries.sort((a, b) => b?.product?.distManufacturer?.name.localeCompare(a?.product?.distManufacturer?.name));
    this.sortByClick.emit(this.entries);
  }

  ngOnInit(): void {
    this.selectModelValue = 1;
    //set 2nd option by default like on production
  }
}
