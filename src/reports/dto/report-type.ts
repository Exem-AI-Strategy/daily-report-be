export type ReportApiResponse<T> = {
  status: 'SUCCESS' | 'FAIL';
  message: string;
  data?: T;
};

export interface ReportData {
  reportId: number;
  userId: number;
  userName: string;
  reportStartDate: string;
  reportEndDate: string;
  title: string;
  content: string;
  comments_count: number;
}

export interface WeekReportData {
  weekStartDate: string;
  weekEndDate: string;
  reports: ReportData[];
}
