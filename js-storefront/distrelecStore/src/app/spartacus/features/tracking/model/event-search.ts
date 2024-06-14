export interface SearchEventPayload {
  track?: 'true';
  pos: string;
  origPos: string;
  page: string;
  pageSize: string;
  origPageSize: string;
  trackQuery: string;
  filterapplied?: string;
  sort?: string;
  searchTerm?: string;
}
