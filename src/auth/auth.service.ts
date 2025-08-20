import { UsersService } from '@/users/users.service';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { User } from '@prisma/client';
import * as bcrypt from 'bcrypt';

@Injectable()
export class AuthService {
  // 필요한 객체 생성 및 의존성 주입
  constructor(
    private usersService: UsersService,
    private jwtService: JwtService,
  ) {}

  async validateUser(email: string, password: string) {
    const user = await this.usersService.findByEmail(email);
    if (user && (await bcrypt.compare(password, user.password))) {
      return user;
    }
    throw new UnauthorizedException('Invalid credentials');
  }

  generateJwt(user: User) {
    const payload = {
      email: user.email,
      sub: user.user_id.toString(),
      role: user.role,
    };
    return this.jwtService.sign(payload);
  }
}
