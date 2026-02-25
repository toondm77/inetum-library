export interface Book {
  id: number;
  title: string;
  isbn: string;
  authorId: number;
  libraryId: number;
  libraryName: string;
  publishedYear: number;
}

export interface Page<T> {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  items: T[];
}
