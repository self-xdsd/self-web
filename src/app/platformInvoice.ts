export interface PlatformInvoice {
  id: number;
  number: string;
  createdAt: string;
  commission: number;
  vat: number;
  total: number;
  paidAt: string;
}
