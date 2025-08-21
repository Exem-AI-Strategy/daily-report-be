import { IsOptional, IsString } from 'class-validator';

export class QueryReportDto {
  @IsOptional()
  @IsString()
  start_date?: string;

  @IsOptional()
  @IsString()
  end_date?: string;

  @IsOptional()
  @IsString()
  user_id?: string;
}
