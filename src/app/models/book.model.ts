export interface Book {
  id: number;
  title: string;
  description?: string;
  authorName?: string;
  isbn: string;
  authorId: number;
  libraryId: number;
  libraryName: string;
  publishedYear: number;
  amountOfPages?: number;
  releaseDate?: string;
  theme?: string;
  bookState?: 'AVAILABLE' | 'BORROWED' | 'RESERVED' | string;
  ageCategory?: string;
  purchasePrice?: number;
  duplicates?: number;
}

export interface Page<T> {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  items: T[];
}
