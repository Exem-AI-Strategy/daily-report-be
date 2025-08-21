import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  Query,
  UseGuards,
  Request,
} from '@nestjs/common';
import { ReportsService } from './reports.service';
import { CreateReportRequestDto } from './dto/create-report.dto';
import { UpdateReportDto } from './dto/update-report.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import {
  ReportApiResponse,
  ReportData,
  WeekReportData,
} from './dto/report-type';

@Controller('reports')
@UseGuards(JwtAuthGuard)
export class ReportsController {
  constructor(private readonly reportsService: ReportsService) {}

  @Post()
  async create(
    @Body() createReportDto: CreateReportRequestDto,
    @Request() req,
  ): Promise<ReportApiResponse<ReportData>> {
    const finalUserId = createReportDto.userId?.toString() || req.user.userId;
    const report = await this.reportsService.createReport(
      createReportDto,
      finalUserId,
    );

    return {
      status: 'SUCCESS',
      message: 'Report가 성공적으로 생성되었습니다.',
      data: report,
    };
  }

  @Get('weekly')
  async findWeeklyReports(
    @Query('startDate') startDate: string,
    @Query('endDate') endDate: string,
    @Request() req,
  ): Promise<ReportApiResponse<WeekReportData>> {
    const reports = await this.reportsService.findWeeklyReports(
      req.user.userId,
      startDate,
      endDate,
    );

    return {
      status: 'SUCCESS',
      message: '주간 Report 조회에 성공했습니다.',
      data: {
        weekStartDate: startDate,
        weekEndDate: endDate,
        reports: reports,
      },
    };
  }

  @Get('daily')
  async findDailyReports(
    @Query('date') date: string,
    @Request() req,
  ): Promise<ReportApiResponse<{ reports: ReportData[] }>> {
    const reports = await this.reportsService.findDailyReports(
      req.user.userId,
      date,
    );

    return {
      status: 'SUCCESS',
      message: '일간 Report 조회에 성공했습니다.',
      data: { reports: reports },
    };
  }

  @Get(':id')
  async findOne(
    @Param('id') id: string,
    @Request() req,
  ): Promise<ReportApiResponse<ReportData>> {
    const report = await this.reportsService.findOne(id, req.user.userId);

    return {
      status: 'SUCCESS',
      message: 'Report 조회에 성공했습니다.',
      data: report,
    };
  }

  @Patch(':id')
  async update(
    @Param('id') id: string,
    @Body() updateReportDto: UpdateReportDto,
    @Request() req,
  ): Promise<ReportApiResponse<ReportData>> {
    const report = await this.reportsService.updateReport(
      id,
      updateReportDto,
      req.user.userId,
    );

    return {
      status: 'SUCCESS',
      message: 'Report가 성공적으로 수정되었습니다.',
      data: report,
    };
  }

  @Delete(':id')
  async remove(
    @Param('id') id: string,
    @Request() req,
  ): Promise<ReportApiResponse<void>> {
    await this.reportsService.removeReport(id, req.user.userId);

    return {
      status: 'SUCCESS',
      message: 'Report가 성공적으로 삭제되었습니다.',
    };
  }
}
