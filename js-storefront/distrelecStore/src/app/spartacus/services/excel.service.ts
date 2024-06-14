import { Injectable } from '@angular/core';
import { saveAs } from 'file-saver';
const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
@Injectable({
  providedIn: 'root',
})
export class ExcelService {
  constructor() {}
  public exportAsExcelFile(json: any[], excelFileName: string): void {
    import('xlsx').then((xlsx) => {
      const worksheet: import('xlsx').WorkSheet = xlsx.utils.json_to_sheet(json);
      const workbook: import('xlsx').WorkBook = {
        Sheets: { data: worksheet },
        SheetNames: ['data'],
      };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: 'xlsx',
        type: 'array',
      });
      this.saveAsExcelFile(excelBuffer, excelFileName);
    });
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE,
    });
    saveAs(data, fileName);
  }
}
