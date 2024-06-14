export interface DownloadData {
  alternativeDownloads?: DownloadFile[];
  code?: string;
  downloads?: DownloadFile[];
  rank?: number;
  title?: string;
}

export interface DownloadFile {
  downloadUrl: string;
  languages: FileLanguage[];
  mimeType: string;
  name: string;
}

export interface FileLanguage {
  active: boolean;
  isocode: string;
  name: string;
  nativeName: string;
  rank?: number;
}
