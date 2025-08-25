/** eslint- */
import {
  Injectable,
  NotFoundException,
  ForbiddenException,
} from '@nestjs/common';
import { PrismaService } from 'prisma/prisma.servies';
import { CreateReportRequestDto } from './dto/create-report.dto';
import { UpdateReportDto } from './dto/update-report.dto';
import { QueryReportDto } from './dto/query-report.dto';
import { report as PrismaReport } from '@prisma/client';

// 응답 타입 정의
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

@Injectable()
export class ReportsService {
  constructor(private prisma: PrismaService) {}

  // Prisma report ->  ReportData로 변환
  async transformToReportData(report: PrismaReport): Promise<ReportData> {
    // 댓글 수 조회
    const commentsCount = await this.prisma.comment.count({
      where: { report_id: report.report_id },
    });

    const user = await this.prisma.user.findUnique({
      where: { user_id: report.user_id },
      select: { name: true },
    });

    return {
      reportId: Number(report.report_id),
      userId: Number(report.user_id),
      userName: user?.name || '',
      reportStartDate: report.report_start_date.toISOString(),
      reportEndDate: report.report_end_date.toISOString(),
      title: report.title,
      content: report.content || '',
      comments_count: commentsCount,
    };
  }

  // Report 생성
  async createReport(
    createReportDto: CreateReportRequestDto,
    userId: string,
  ): Promise<ReportData> {
    const startDate = new Date(
      createReportDto.reportStartDate.replace(' ', 'T'),
    );
    const endDate = new Date(createReportDto.reportEndDate.replace(' ', 'T'));

    const report = await this.prisma.report.create({
      data: {
        user_id: BigInt(userId),
        report_start_date: startDate,
        report_end_date: endDate,
        title: createReportDto.title,
        content: createReportDto.content || '',
      },
    });

    return this.transformToReportData(report);
  }

  // Report 단건 조회
  async findOne(reportId: string, userId: string): Promise<ReportData> {
    const report = await this.prisma.report.findUnique({
      where: { report_id: BigInt(reportId) },
    });

    if (!report) {
      throw new NotFoundException('Report not found');
    }

    // 본인 또는 ADMIN만 조회 가능
    if (report.user_id.toString() !== userId) {
      throw new ForbiddenException('Access denied');
    }

    return this.transformToReportData(report);
  }

  // Report 수정
  async updateReport(
    reportId: string,
    updateReportDto: UpdateReportDto,
    userId: string,
  ): Promise<ReportData> {
    await this.findOne(reportId, userId); // 권한 확인

    const updateData: any = {
      user_id: BigInt(userId),
      report_start_date: updateReportDto.reportStartDate,
      report_end_date: updateReportDto.reportEndDate,
      title: updateReportDto.title,
      content: updateReportDto.content || '',
      report_id: BigInt(reportId),
    };
    if (updateReportDto.reportStartDate) {
      updateData.report_start_date = new Date(updateReportDto.reportStartDate);
    }
    if (updateReportDto.reportEndDate) {
      updateData.report_end_date = new Date(updateReportDto.reportEndDate);
    }

    const updatedReport = await this.prisma.report.update({
      where: { report_id: BigInt(reportId) },
      data: updateData,
    });

    return this.transformToReportData(updatedReport);
  }

  // Report 삭제
  async removeReport(reportId: string, userId: string): Promise<void> {
    await this.findOne(reportId, userId); // 권한 확인

    await this.prisma.report.delete({
      where: { report_id: BigInt(reportId) },
    });
  }

  // 주간 Report 조회
  async findWeeklyReports(
    userId: string,
    startDate: string,
    endDate: string,
  ): Promise<ReportData[]> {
    const reports = await this.prisma.report.findMany({
      where: {
        user_id: BigInt(userId),
        report_start_date: {
          gte: new Date(startDate),
          lte: new Date(endDate),
        },
      },
      orderBy: { report_start_date: 'desc' },
    });

    return Promise.all(
      reports.map((report) => this.transformToReportData(report)),
    );
  }

  // 일간 Report 조회
  async findDailyReports(userId: string, date: string): Promise<ReportData[]> {
    const targetDate = new Date(date);
    const startOfDay = new Date(targetDate.setHours(0, 0, 0, 0));
    const endOfDay = new Date(targetDate.setHours(23, 59, 59, 999));

    const reports = await this.prisma.report.findMany({
      where: {
        user_id: BigInt(userId),
        report_start_date: {
          gte: startOfDay,
          lte: endOfDay,
        },
      },
      orderBy: { report_start_date: 'desc' },
    });

    return Promise.all(
      reports.map((report) => this.transformToReportData(report)),
    );
  }

  // 사용자별 Report 목록 조회
  async findUserReports(
    userId: string,
    query: QueryReportDto,
  ): Promise<ReportData[]> {
    const where: any = { user_id: BigInt(userId) };

    if (query.start_date && query.end_date) {
      where.report_start_date = {
        gte: new Date(query.start_date),
        lte: new Date(query.end_date),
      };
    }

    const reports = await this.prisma.report.findMany({
      where,
      orderBy: { report_start_date: 'desc' },
    });

    return Promise.all(
      reports.map((report) => this.transformToReportData(report)),
    );
  }
}
