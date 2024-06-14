export interface ProductAvailability {
  backorderDeliveryDate?: unknown;
  backorderQuantity: number;
  deliveryTimeBackorder: string;
  detailInfo: boolean;
  leadTimeErp: number;
  productCode: string;
  requestedQuantity: number;
  statusLabel: string;
  stockLevelPickup: StockLevelPickup[];
  stockLevelTotal: number;
  stockLevels: StockLevel[];
}

export interface StockLevel {
  available: number;
  deliveryTime: string;
  external: boolean;
  fast: boolean;
  mview: string;
  replenishmentDeliveryTime: string;
  replenishmentDeliveryTime2: string;
  vendorNumber?: string;
  waldom: boolean;
  warehouseId: string;
  leadTime?: string;
}

export interface StockLevelPickup {
  stockLevel: number;
  warehouseCode: string;
  warehouseName: string;
}

export interface AvailabilityResponse {
  productAvailability: ProductAvailability[];
}

export interface AvailableStockFileExport {
  deliveryTime: string;
  quantityAvailable: number | string;
}
