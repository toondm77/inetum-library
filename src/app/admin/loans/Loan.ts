interface Loan {
  id: number;
  loanDate: string;
  returnDate: string;
  status: string;
  personId: number;
  personName: string;
  loanRuleId: number;
  bookIds: number[];
}