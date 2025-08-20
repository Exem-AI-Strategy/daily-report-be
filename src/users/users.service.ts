import { Injectable } from '@nestjs/common';
import { PrismaService } from 'prisma/prisma.servies';
import * as bcrypt from 'bcrypt';
import { User } from '@prisma/client';
import { createUserDto } from './dto/create-user.dto';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}

  async createUser(dto: createUserDto): Promise<User> {
    const hashedPassword = await bcrypt.hash(dto.password, 10);
    return this.prisma.user.create({
      data: { name: dto.name, email: dto.email, password: hashedPassword },
    });
  }

  findByEmail(email: string) {
    return this.prisma.user.findUnique({ where: { email } });
  }
}
