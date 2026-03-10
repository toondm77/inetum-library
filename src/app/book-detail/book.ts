export interface BookUpdatePayload {
  title: string;
  description: string;
  isbn: string;
  authorId: number;
  libraryId: number;
  publishedYear: number;
  amountOfPages: number;
  releaseDate: string;
  theme: string;
  bookState: string;
  ageCategory: string;
  purchasePrice: number;
  duplicates: number;
}