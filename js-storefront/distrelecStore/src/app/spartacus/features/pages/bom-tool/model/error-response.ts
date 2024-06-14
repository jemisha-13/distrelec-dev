export interface ErrorResponse {
  error: {
    errors: {
      message: string;
      type: string;
    }[];
  };
}
