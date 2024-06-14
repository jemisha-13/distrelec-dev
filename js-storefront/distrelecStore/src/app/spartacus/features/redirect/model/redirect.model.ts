export interface DynamicMappingRule {
  shortURL: string;
  destinationURL: string;
  urlMatchExpression: string;
  permanent: boolean;
}
