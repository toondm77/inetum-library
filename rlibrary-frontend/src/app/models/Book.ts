export interface Book {
  id: number;
  title: string;
  description: string;
  authorName: string;
  isbn: string;
  authorId: number;
  libraryId: number;
  libraryName: string;
  publishedYear: number;
  amountOfPages: number;
  releaseDate: string;
  theme: string;
  bookState: 'AVAILABLE' | 'BORROWED' | 'LOST' | string;
  ageCategory: string;
  purchasePrice: number;
  duplicates: number;
  coverImage?: string;
}