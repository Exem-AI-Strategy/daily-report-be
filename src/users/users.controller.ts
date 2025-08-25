import { Body, Controller, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { CreateUserDto, SignUpResponseData } from './dto/create-user.dto';

@Controller('users')
export class UsersController {
  constructor(private usersService: UsersService) {}

  @Post('signup')
  async signup(@Body() dto: CreateUserDto) {
    const user = await this.usersService.createUser(dto);
    const data: SignUpResponseData = {
      userId: Number(user.user_id),
      userName: user.name,
      email: user.email,
      role: user.role,
    };
    return { status: 'SUCCESS', data: data };
  }
}
