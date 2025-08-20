import { Body, Controller, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { createUserDto, signUpResponseData } from './dto/create-user.dto';

@Controller('users')
export class UsersController {
  constructor(private usersService: UsersService) {}

  @Post('signup')
  async signup(@Body() dto: createUserDto) {
    const user = await this.usersService.createUser(dto);
    const data: signUpResponseData = {
      userId: Number(user.user_id),
      userName: user.name,
      email: user.email,
      role: user.role,
    };
    return { status: 'SUCCESS', data: data };
  }
}
