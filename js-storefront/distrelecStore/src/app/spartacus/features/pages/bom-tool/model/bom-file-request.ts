export interface BomFileRequest {
  fileName: string;
  entry: BomFileRequestEntry[];
}

export interface BomFileRequestEntry {
  code: string; // Searched product code
  productCode: string; // Matched product code or ""
  customerReference?: string;
  quantity: string;
}
