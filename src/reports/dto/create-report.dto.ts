import { IsString, IsOptional, IsNotEmpty, IsNumber } from 'class-validator';

export class CreateReportRequestDto {
  @IsOptional()
  reportId?: number;

  @IsOptional()
  userId?: number;

  @IsNotEmpty()
  @IsString()
  reportStartDate: string;

  @IsNotEmpty()
  @IsString()
  reportEndDate: string;

  @IsNotEmpty()
  @IsString()
  title: string;

  @IsOptional()
  @IsString()
  content?: string;
}

export class CreateReportResponseDto {
  @IsOptional()
  reportId?: number;

  @IsOptional()
  userId?: number;

  @IsNotEmpty()
  @IsString()
  reportStartDate: string;

  @IsNotEmpty()
  @IsString()
  reportEndDate: string;

  @IsNotEmpty()
  @IsString()
  title: string;

  @IsOptional()
  @IsString()
  content?: string;

  @IsNotEmpty()
  @IsNumber()
  commentsCount: number;
}
